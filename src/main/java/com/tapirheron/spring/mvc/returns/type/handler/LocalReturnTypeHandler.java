package com.tapirheron.spring.mvc.returns.type.handler;

import com.tapirheron.spring.mvc.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 本地文件返回类型处理器，用于处理本地静态资源文件的返回
 * <p>
 * 支持模板变量替换，可以将数据渲染到HTML模板中
 * </p>
 *
 * @author TapirHeron
 * @since 1.0
 */
public class LocalReturnTypeHandler implements ReturnTypeHandler{
    /**
     * 模板变量匹配模式，用于匹配${variable}格式的变量
     */
    private static final Pattern PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    /**
     * 处理本地文件类型的返回结果
     *
     * @param result 返回结果对象，应为ModelAndView类型
     * @param req    HTTP请求对象
     * @param resp   HTTP响应对象
     */
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

    /**
     * 渲染模板，替换模板中的变量
     *
     * @param template 模板内容
     * @param context  上下文数据
     * @return 渲染后的模板内容
     */
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
