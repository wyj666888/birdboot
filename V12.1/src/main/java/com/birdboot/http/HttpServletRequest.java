package com.birdboot.http;


import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
    private Map<String,String> headers = new HashMap<>();
    /*
        V12新增内容
        例如:
        uri--->/regUser?username=fancq&password=123456&nickname=chuanqi&age=22
        新添加的三个属性最终应当保存的信息如下
        requestURI:/regUser
        queryString:username=fancq&password=123456&nickname=chuanqi&age=22
        parameters:
            key             value
          username          fancq
          password          123456
          nickname          chuanqi
          age               22
     */
    private String requestURI;//保存uri中"?"左侧的请求部分
    private String queryString;//保存uri中"?"右侧的参数部分
    private Map<String,String> parameters = new HashMap<>();//保存每一组参数

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
        uri = data[1].substring(1);
        protocol = data[2];

        System.out.println("method:"+method);//method:GET
        System.out.println("uri:"+uri);//uri:/index.html
        //进一步解析Uri
        parseURI(uri);

        System.out.println("protocol:"+protocol);//protocol:HTTP/1.1
    }
    //进一步解析uri
    private void parseURI(String uri){
        int index = uri.indexOf('?');
        if (index == -1) { // URI中不含参数
            requestURI = uri;
        } else { // URI中含有参数
            requestURI = uri.substring(0, index);
            queryString = uri.substring(index + 1);
            String[] paramPairs = queryString.split("&");
            for (String pair : paramPairs) {
                String[] param = pair.split("=");
                parameters.put(param[0],param.length==2?param[0]:"");
            }
        }
        System.out.println("requestURI: " + requestURI);
        System.out.println("queryString: " + queryString);
        System.out.println("parameters: " + parameters);
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
            headers.put(data[0],data[1]);
        }
        System.out.println("headers:"+headers);
    }
    //解析消息正文
    private void parseContent(){

    }

    public void run(){

    }
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
        return headers.get(name);
    }
}
