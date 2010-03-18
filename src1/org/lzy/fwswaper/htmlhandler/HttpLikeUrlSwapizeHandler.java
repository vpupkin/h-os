package org.lzy.fwswaper.htmlhandler;

import java.net.URL;
import java.util.logging.Logger;

import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;

import org.lzy.fwswaper.htmlhandler.htmlparserex.HeaderLinkTag;
import org.lzy.fwswaper.htmlhandler.htmlparserex.HtmlparserUrlModifier;
import org.lzy.fwswaper.htmlhandler.htmlparserex.ScriptExTag;

public class HttpLikeUrlSwapizeHandler implements IHtmlHandler {

	private static final Logger log = Logger
			.getLogger(HttpLikeUrlSwapizeHandler.class.getName());

	private URL base = null;

	public HttpLikeUrlSwapizeHandler(URL base) {
		this.base = base;
	}

	public String handling(String html, String charset) throws Exception {

		log
				.info(String
						.format(
								"Before handling html content ('%s' encoding charset) => '%s'.",
								charset, html));

		// Use htmlparser library to parse html.
		// TODO: 'nekohtml' html parser lib may be faster and better then 'htmlparser' lib.
		// TODO: Is this html parse function need to refactor extract to an interface/implement? and configurable?

		PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
		factory.registerTag(new HeaderLinkTag());
		factory.registerTag(new ScriptExTag());

		Parser parser = Parser.createParser(html, charset);
		parser.setNodeFactory(factory);

		// Match and modify link image and frame tag url address.
		HtmlparserUrlModifier modifier = new HtmlparserUrlModifier(this.base);

		parser.visitAllNodesWith(modifier);

		html = modifier.getModifiedResult();

		log
				.info(String
						.format(
								"Handling completed. Html content ('%s' encoding charset) => '%s'.",
								charset, html));

		log.info("Http like url swapize handling well done.");

		return html;
	}
}
