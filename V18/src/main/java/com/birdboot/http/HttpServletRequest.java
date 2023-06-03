package com.birdboot.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
/**
 * 请求对象
 * 该类的每一个实例表示HTTP协议要求的浏览器发送给服务端的一个请求
 * 每个请求由三个部分构成:
 * 请求行，消息头，消息正文
 *
 */
public class HttpServletRequest {
    /**
     * 将类的属性声明为private可以实现封装的概念
     * 如果属性不被声明为private，那么其他类就可以直接访问和修改属性的值，
     * 这样可能会破坏类的封装性并造成潜在的安全风险
     */
    private Socket socket;
    //请求行相关信息
    private String method;//请求方式
    private String uri;//抽象路径
    private String protocol;//协议版本
    private Map<String,String> headers = new HashMap<>();//保存响应头参数
    //请求路径
    private String requestURI;
    //参数信息
    private String queryString;
    //创建Map,记为parameters,用来保存参数
    private Map<String,String> parameters = new HashMap<>();

    public HttpServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;
        //1解析请求行
        parseRequestLine();
        //2解析消息头
        parseHeaders();
        //3解析消息正文
        parseContent();
    }
    //解析请求行
    private void parseRequestLine() throws IOException, EmptyRequestException {

        String line = this.readLine();

        if (line.isEmpty()){
            throw new EmptyRequestException();
        }

        System.out.println("请求行!!!!!!!!!!!!!!!!!!!!!!!:"+line);

        String[] data = line.split("\\s");
        method = data[0];
        uri = data[1];
        protocol = data[2];
        //进一步解析Uri
        parseURI();

        System.out.println("method:"+method);//method:GET
        System.out.println("uri:"+uri);//uri:/index.html
        System.out.println("protocol:"+protocol);//protocol:HTTP/1.1
    }
    //进一步解析uri
    private void parseURI(){
        int index = uri.indexOf('?');
        if (index == -1) { // URI中不含参数
            //保存uri中"?"左侧的请求部分
            requestURI = uri;
        } else { // URI中含有参数
            requestURI = uri.substring(0, index);
            //保存uri中"?"右侧的参数部分
            queryString = uri.substring(index + 1);
            //保存注册表的参数
            parseParameters(queryString);
        }
        System.out.println("requestURI: " + requestURI);
        System.out.println("queryString: " + queryString);
        System.out.println("parameters: " + parameters);
    }
    //解析
    private void parseParameters(String line){
        try {
            //先对参数解码
            line = URLDecoder.decode(line,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] paramPairs = line.split("&");
        for (String pair : paramPairs) {
            String[] param = pair.split("=",2);
            parameters.put(param[0],param.length==2?param[1]:"");
        }
    }


    //解析消息头
    private void parseHeaders() throws IOException {
        while(true) {//while循环是因为浏览器发送多少个消息头不确定
            String line = readLine();
            if(line.isEmpty()){//如果readLine返回空字符串，说明单独读取了回车+换行
                break;//因为单独的回车+换行表示消息头部分发送完毕
            }
            System.out.println("消息头:" + line);
            //将消息头按照冒号空格拆分为消息头名字和值并以key,value形式保存到headers中
            String[] data = line.split(":\\s");
            //改造:将消息头的名字转换为全小写再保存,以便获取消息头时忽略大小写问题
            headers.put(data[0].toLowerCase(),data[1]);
        }
        System.out.println("headers:"+headers);
    }
    //解析消息正文
    private void parseContent(){
        //根据请求方式是否为POST决定是否要解析正文
        if ("post".equalsIgnoreCase(method)){
            //根据消息头Content-Length来确定正文长度
            String contentLength = getHeader("Content-Length");
            if (contentLength!=null){
                //确定正文长度
                int cl = Integer.parseInt(contentLength);
                System.out.println("正文长度!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!:"+cl);
                //读取正文数据
                byte[] data = new byte[cl];
                try {
                    InputStream in = socket.getInputStream();
                    in.read(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //根据Content-Type来分析正文类型并进行对应的解析
                String contentType = getHeader("Content-Type");
                if (contentType!=null){
                    if ("application/x-www-form-urlencoded".equals(contentType)){
                        //将data按照StandardCharsets.ISO_8859_1转换为字符串
                        String line = new String(data, StandardCharsets.ISO_8859_1);
                        System.out.println("正文内容:"+line);

                        parseParameters(line);
                    }//后续解析其他类型的正文
//                    else if ("xxxxx".equals(contentType)){}
                }
            }
        }
    }

    /**
     * 读取客户端发送过来的一行字符串
     * @return
     */
    private String readLine() throws IOException {
        InputStream in = socket.getInputStream();
        char pre='a',cur='a';//pre表示上次读取的字符,cur表示本次读取的字符
        StringBuilder builder = new StringBuilder();//记录已读取的一行字符串的内容
        int d;//每次读取到的字节,记为d
        while ((d=in.read())!=-1){
            cur = (char)d;//本次读取的字符
            if(pre==13&&cur==10){//是否已经连续读取到了回车+换行
                break;
            }
            builder.append(cur);//将本次读取的字符拼接
            pre = cur;//再下次读取前,将本次读取的字符赋值给pre,记为上次读取的字符
        }
        //返回时用表达式比较优雅^_^
        return builder.toString().trim();
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }
    //一般Map的返回值不会是Map
    //根据消息头的名字获取对应的值,
    public String getHeader(String name) {
        //改造:获取消息头时,也要将名字转换为小写
        return headers.get(name.toLowerCase());
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }
}
