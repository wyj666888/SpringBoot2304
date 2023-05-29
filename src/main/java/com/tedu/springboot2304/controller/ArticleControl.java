package com.tedu.springboot2304.controller;

import com.tedu.springboot2304.entity.Article;
import com.tedu.springboot2304.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ArticleControl {
    private static File articleDir;
    static {
        articleDir = new File("./article");
        if(!articleDir.exists()){
            articleDir.mkdirs();
        }
    }

    @RequestMapping("/writeArticle")
    public void writeArticle(HttpServletRequest request, HttpServletResponse response){
        System.out.println(":开始发表文章....");
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String content = request.getParameter("content");
        System.out.println(title+","+author+","+content);

        if (title==null||title.isEmpty()||
                author==null||author.isEmpty()||
                content==null||content.isEmpty()){
            try {
                response.sendRedirect("/info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        Article article1 = new Article(title,author,content);
        File article = new File(articleDir,title+".obj");

        if (article.exists()){
            try {
                response.sendRedirect("/have_article.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try (
        FileOutputStream fos = new FileOutputStream(article);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        )
         {
            oos.writeObject(article1);
            response.sendRedirect("/article_success.html");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping("/articleList")
    public void articleList(HttpServletRequest request, HttpServletResponse response){
        System.out.println("开始处理文章列表的动态页面!!!");
        File[] subs = articleDir.listFiles(f->f.getName().endsWith(".obj"));
        List<Article> articleList = new ArrayList<>();
        for (File sub : subs){
            try {
                FileInputStream fis = new FileInputStream(sub);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Article article = (Article)ois.readObject();
                articleList.add(article);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println(articleList);
        try {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter pw = response.getWriter();
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"en\">");
            pw.println("<head>");
            pw.println("<meta charset=\"UTF-8\">");
            pw.println("<title>文章列表</title>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("<center>");
            pw.println("<h1>文章列表</h1>");
            pw.println("<table border=\"1\">");
            pw.println("<tr><td>标题</td><td>作者</td><td align="+"center"+" >内容</td></tr>");

            for(Article article : articleList) {
                pw.println("<tr>");
                pw.println("<td>"+article.getTitle()+"</td>");
                pw.println("<td>"+article.getAuthor()+"</td>");
                pw.println("<td align="+"center"+" >"+article.getContent()+"</td>");
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
