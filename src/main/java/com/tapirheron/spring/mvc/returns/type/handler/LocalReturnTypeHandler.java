package com.tapirheron.spring.mvc.returns.type.handler;

import com.tapirheron.spring.mvc.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalReturnTypeHandler implements ReturnTypeHandler{
    private static final Pattern PATTERN = Pattern.compile("\\$\\{(.*?)\\}");
    @Override
    public void handle(Object result, HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html;charset=UTF-8");
        ModelAndView mv = (ModelAndView) result;
        String view = mv.getView();
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(view);
        try(resourceAsStream) {
            String html = null;
            if (resourceAsStream != null) {
                html = new String(resourceAsStream.readAllBytes());
                html = renderTemplate(html, mv.getContext());
            }
            if (html != null) {
                resp.getWriter().write(html);
            }
        } catch (IOException ignore) {}
    }

    public static String renderTemplate(String template, Map<String, String> context) {
        Matcher matcher = PATTERN.matcher(template);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = context.getOrDefault(key, "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
