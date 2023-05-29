package com.tedu.springboot2304.controller;

import com.tedu.springboot2304.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 使用当前类处理与用户相关的业务操作
 * 比如:登录，注册，修改密码等等
 *
 * controller在SpringBoot上的创建要求
 * 1:包必须要定义在启动类所在的包中
 * 2:我们定义的Controller类上必须要添加一个注解"@Controller"
 *   不添加该注解，Spring框架不会调用这个类来处理业务
 * 3:添加处理某个请求的业务方法，并且该方法上要添加注解"@RequestMapping"
 *   且该注解上要添加一个参数，该参数值与其对应的请求路径一致即可
 *   例如:
 *   reg.html页面上表单我们定义了action="/regUser"，意味着当用户在注册页面上
 *   输入注册信息后点击注册按钮提交表单时，表单提交的路径为"/regUser"
 *   我们希望该请求路径可以被UserController中的reg方法处理时，该方法上就要添加
 *   注解@RequestMapping("/regUser") 这里参数要与页面表单中action值一致。
 *   此时该表单提交后Spring框架就会自动调用到reg方法来处理了
 *
 */
@Controller
public class UserController {
    //创建静态变量userDir,File类型,表示用户信息储存的目录
    private static File userDir;
    //静态块,用于初始化用户信息存储的目录
    static {
        userDir = new File("./users");
        if (!userDir.exists()){
            userDir.mkdirs();
        }
    }
    @RequestMapping("/regUser")
    public void reg(HttpServletRequest request,HttpServletResponse response){
             /*
            1:获取表单提交的注册信息
            2:将用户注册信息保存在磁盘上
            3:回复用户注册结果页面
         */
        //1
        //regUser?username=范传奇&password=123456&nickname=传奇&age=22
        /*
            HttpServletRequest为请求对象，保存着浏览器发送过来的所有内容
            我们通过请求对象获取表单提交的数据时，使用如下方法:
            String getParameter(String name)
            该方法可以获取浏览器提交的某个参数的值。
            实际应用中，我们在getParameter中传入的参数要与页面表单中对应
            输入框上name=""的值一致
            例如:
            注册页面，用户名输入框<input name="username" type="text">
            我们要获取该输入框的值，这里需要:
             String username = request.getParameter("username");
         */
        System.out.println("开始处理用户注册!!!");
        //获取请求参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");
        System.out.println(username+","+password+","+nickname+","+ageStr);
        //判断输入信息是否符合格式要求,字符串类型不能为null,不能为空串,用正则表达式约束
        if(username==null||username.isEmpty()||
                password==null||password.isEmpty()||
                nickname==null||nickname.isEmpty()||
                ageStr==null||ageStr.isEmpty()||
                  !ageStr.matches("[0-9]+")){
            try {
                //不符合,则跳转到错误页面
                response.sendRedirect("/reg_info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        //年龄字符串需转换为int型,才能存进User对象
        int age = Integer.parseInt(ageStr);
        //创建用户对象
        User user = new User(username,password,nickname,age);
        //建立userFile,File类型,在userDir目录下,用username+".obj"作为文件名
        File userFile = new File(userDir,username+".obj");
        //判断文件名是否已经存在
        if (userFile.exists()){
            try {
                //存在,则跳转到已存在页面
                response.sendRedirect("/have_user.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try (
                //创建文件输出流,输出userFile文件
            FileOutputStream fos = new FileOutputStream(userFile);
            //创建对象输出流,输出到fos文件流
            ObjectOutputStream oos = new ObjectOutputStream(fos);
        )
                //将user对象写入对象输出流
        {oos.writeObject(user);
            //跳转到注册成功页面
            response.sendRedirect("/reg_success.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @RequestMapping("/loginUser")
    public void login(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始登录......");
        //获取请求参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username+","+password);
        if (username==null||username.isEmpty()||
            password==null||password.isEmpty()){
            try {
                response.sendRedirect("/login_info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        //建立userFile,File类型,在userDir目录下,用username+".obj"作为文件名
        File userFile = new File(userDir, username + ".obj");
        //判断文件是否存在
        if (userFile.exists()) {
            try (FileInputStream fis = new FileInputStream(userFile);
                 ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                //将ois对象输入流读取到User对象中,对象名user
                User user = (User) ois.readObject();
                System.out.println(user);
                //比较参数与对象属性是否一致
                if (user.getUsername().equals(username) &&
                        user.getPassword().equals(password)) {
                    response.sendRedirect("/login_success.html");
                } else {
                    response.sendRedirect("/login_fail.html");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            try {
                response.sendRedirect("/login_info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @RequestMapping("/userList")
    public void userList(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始处理用户列表的动态页面!!!");
        //创建数组subs,存储userDir目录下得子类,以".obj"结尾的文件
        File[] subs = userDir.listFiles(f->f.getName().endsWith(".obj"));
        //创建ArrayList集合,User对象类型,名userList,使用List接口
        //这样可以使代码更加灵活，可以轻松替换为其他List实现类，而不会影响到其他代码。而且List是一个更一般化的接口，其定义更广泛，可支持更多的操作，因此使用List会更加通用。
        List<User> userList = new ArrayList<>();
        //遍历数组subs的File文件,
        for(File sub : subs){
            try(FileInputStream fis = new FileInputStream(sub);
                ObjectInputStream ois = new ObjectInputStream(fis);
                ) {
                User user = (User)ois.readObject();
                //将对象user添加到userList集合中
                userList.add(user);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println(userList);

        try {
            //跳转动态页面
            response.setContentType("text/html;charset=utf-8");
            /*
            * 第一行代码是设置HTTP响应的Content-Type头部，告诉客户端响应内容的类型和编码格式。
            * 在这里，设置的Content-Type类型为"text/html"，编码格式为"utf-8"。也就是说，
* 服务器响应的内容是HTML格式的文本，并且使用UTF-8编码格式。第二行代码是获取HTTP响应输出流PrintWriter对象。
            * PrintWriter是一个字符流，它允许写入字符数据到客户端响应的输出流中。通过这个输出流，
            * 可以将HTTP响应的HTML页面内容输出到客户端的浏览器中。通常，在输出HTML页面内容之前，
            * 需要设置Content-Type头部，告诉浏览器如何处理输出的数据。因此，
            * 这两行代码一起实现了设置HTTP响应的类型和编码格式，并将HTML页面内容输出到客户端的浏览器中*/
            PrintWriter pw = response.getWriter();
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"en\">");
            pw.println("<head>");
            pw.println("<meta charset=\"UTF-8\">");
            pw.println("<title>用户列表</title>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("<center>");
            pw.println("<h1>用户列表</h1>");
            pw.println("<table border=\"1\">");
            pw.println("<tr><td>用户名</td><td>密码</td><td>昵称</td><td>年龄</td></tr>");
            //遍历对象集合
            for(User user : userList) {
                pw.println("<tr>");
                pw.println("<td>"+user.getUsername()+"</td>");
                pw.println("<td>"+user.getPassword()+"</td>");
                pw.println("<td>"+user.getNickname()+"</td>");
                pw.println("<td>"+user.getAge()+"</td>");
                pw.println("</tr>");
            }

            pw.println("</table>");
            pw.println("</center>");
            pw.println("</body>");
            pw.println("</html>");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
