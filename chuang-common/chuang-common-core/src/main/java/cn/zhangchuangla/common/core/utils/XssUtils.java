package cn.zhangchuangla.common.core.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

/**
 * XSS 工具类：
 * 1. sanitizeHtml: 以严格白名单清洗富文本，保留常用安全标签与必要属性
 * 2. extractPlainText: 从富文本中仅提取纯文字（不包含图片、表格等）
 * 3. extractTextTableImage: 提取文字与图片、表格（仅结构化文本 + 图片链接），丢弃其他不必要内容
 *
 * @author Chuang
 */
public final class XssUtils {

    private XssUtils() {
    }

    /**
     * 使用白名单清洗 HTML，移除潜在的 XSS 向量。
     * 白名单允许的标签：p, br, b, i, u, em, strong, a(href,title,target,rel), ul, ol, li, span, div,
     * h1-h6, blockquote, code, pre, img(src,alt,title), table, thead, tbody, tr, th, td
     */
    public static String sanitizeHtml(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }

        Safelist safelist = Safelist.relaxed()
                .addTags("table", "thead", "tbody", "tr", "th", "td")
                .addAttributes("a", "href", "title", "target", "rel")
                .addAttributes("img", "src", "alt", "title")
                .addAttributes("table", "border", "cellpadding", "cellspacing")
                .addProtocols("a", "href", "http", "https", "mailto")
                .addProtocols("img", "src", "http", "https", "data");

        // 强制为链接添加 rel=noopener 等，阻止钓鱼和跳转利用
        String cleaned = Jsoup.clean(html, safelist);

        Document document = Jsoup.parse(cleaned);
        for (Element a : document.select("a[href]")) {
            a.attr("rel", "noopener noreferrer nofollow");
            if (!a.hasAttr("target")) {
                a.attr("target", "_blank");
            }
        }
        return document.body().html();
    }

    /**
     * 仅提取纯文本内容（无任何标签）。
     */
    public static String extractPlainText(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        return Jsoup.parse(html).text();
    }

    /**
     * 提取文字、表格与图片：
     * - 文字：保留为段落 p 和换行 br
     * - 表格：保留 table/thead/tbody/tr/th/td，剔除样式与事件属性
     * - 图片：保留 img 的 src/alt/title 属性
     * - 其余标签全部移除
     */
    public static String extractTextTableImage(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        Safelist safelist = new Safelist()
                .addTags("p", "br", "table", "thead", "tbody", "tr", "th", "td", "img")
                .addAttributes("img", "src", "alt", "title");
        String kept = Jsoup.clean(html, safelist);

        // 二次处理：去掉 table/td/th 上的 style/class 等属性（如有残留）
        Document document = Jsoup.parse(kept);
        document.select("table, thead, tbody, tr, th, td").forEach(Element::clearAttributes);
        return document.body().html();
    }
}


