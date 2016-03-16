package com.smartlittlepeople.tinyrhymes.mirror;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateRenderer {

	public static String render(ServletContext ctx, String template, Object data)
			throws IOException, ServletException {
		Configuration config = new Configuration();
		config.setServletContextForTemplateLoading(ctx, "WEB-INF/views");
		config.setDefaultEncoding("UTF-8");
		Template ftl = config.getTemplate(template);
		try {
			StringWriter writer = new StringWriter();
			ftl.process(data, writer);
			return writer.toString();
		} catch (TemplateException e) {
			throw new ServletException("Problem while processing template", e);
		}
	}

	public static String renderSongBundleCover(ServletContext ctx,
			Map<String, String> data) throws IOException, ServletException {

		return render(ctx, "glass/cover.ftl", data);

	}

	public static String renderSongBundleCategorySelector(ServletContext ctx,
			Map<String, String> data) throws IOException, ServletException {

		return render(ctx, "glass/category.ftl", data);

	}
	
	public static String renderSongBundleItem(ServletContext ctx,
			Map<String, String> data) throws IOException, ServletException {

		return render(ctx, "glass/song.ftl", data);

	}
}
