package org.lzy.fwswaper.htmlhandler.htmlparserex;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.tags.FormTag;
import org.htmlparser.tags.FrameTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.visitors.UrlModifyingVisitor;

import org.lzy.fwswaper.FwswaperServlet;
import org.lzy.fwswaper.htmlhandler.HtmlHandlerHelper;
import org.lzy.fwswaper.util.Base64Coder;
import org.lzy.fwswaper.util.ExceptionUtils;

public class HtmlparserUrlModifier extends UrlModifyingVisitor
{
	private static final Logger log =
		Logger.getLogger(HtmlparserUrlModifier.class.getName());

	private URL base = null;
	
	public HtmlparserUrlModifier(URL base)
	{
		super("");
		
		this.setBaseUrl(base);
	}

	public void setBaseUrl(URL base)
	{
		if (!HtmlHandlerHelper.isHttpLikeProtocolUrl(base))
			throw new IllegalArgumentException(String.format(
					"Base url argument '%s' is not http like protocol. " +
					"They are not prefix with '%s' or '%s'", this.base.toString(),
					HtmlHandlerHelper.HttpProtocol, HtmlHandlerHelper.HttpsProtocol));

		this.base = base;
	}
	
    public void visitStringNode(Text stringNode)
    {
    	// MUST override this method.
    	// Super class UrlModifingVistor wrote: 'this.modifiedResult.append (stringNode.toHtml());'.
    	// It will append stringNode.toHtml() conent to outside of <html/> tag if not override it.
    }

	public void visitTag(Tag tag)
	{
		try {
	    	if (tag instanceof LinkTag) {

				LinkTag link = (LinkTag) tag;

				log.info(String.format("Found link: '%s' => '%s'.", link.getLinkText(), link.extractLink()));

				if (link.isHTTPLikeLink())
					link.setLink(this.modifying(new URL(base, link.getLink())));
				
			} else if (tag instanceof HeaderLinkTag) {

				HeaderLinkTag link = (HeaderLinkTag) tag;

				log.info(String.format("Found head link: '%s' => '%s'.", link.getLinkText(), link.getLink()));
				
				URL url = new URL(base, link.getLink());
				
				if (HtmlHandlerHelper.isHttpLikeProtocolUrl(url))
					link.setLink(this.modifying(url));

			} else if (tag instanceof ScriptExTag) {

				ScriptExTag script = (ScriptExTag) tag;

				String src = script.getSrc();

				if ((src != null) && (src.length() > 0)) {
					
					log.info(String.format("Found script: '%s' => '%s'.", script.getLanguage(), src));

					URL url = new URL(base, src);

					if (HtmlHandlerHelper.isHttpLikeProtocolUrl(url))
						script.setSrc(this.modifying(url));
				}

			} else if (tag instanceof ImageTag) {	

				ImageTag img = (ImageTag) tag;	

				log.info(String.format("Found image => '%s'.", img.getImageURL()));
				
				URL url = new URL(base, img.getImageURL());
				
				if (HtmlHandlerHelper.isHttpLikeProtocolUrl(url))
					img.setImageURL(this.modifying(url));
				
			} else if (tag instanceof FrameTag) {

				FrameTag frame = (FrameTag) tag;
				
				log.info(String.format("Found frame: '%s' => '%s'.", frame.getText(), frame.getFrameLocation()));
				
				URL url = new URL(base, frame.getFrameLocation());
				
				if (HtmlHandlerHelper.isHttpLikeProtocolUrl(url))
					frame.setFrameLocation(this.modifying(url));
				
			} else if (tag instanceof FormTag) {
				
				FormTag form = (FormTag) tag;
				
				log.info(String.format("Found form: '%s' => (%s) '%s'.", form.getFormName(), form.getFormMethod(), form.extractFormLocn()));
				
				URL url = new URL(base, form.extractFormLocn());
				
				if (HtmlHandlerHelper.isHttpLikeProtocolUrl(url))
					form.setFormLocation(this.modifying(url));
			}

	    	// TODO: Try to add FrameSetTag match.
	    	// test: http://localhost:8080/swap/http://bbs.csdn.net

		} catch(Exception e) {
			log.warning(String.format("Modify url failed. Exception message: '%s'.",
					ExceptionUtils.getStackTrace(e)));
		}

		super.visitTag(tag);
	}
	
	// TODO: This modifying function may be need to refactor extract to an interface/implement.
	protected String modifying(URL url) throws MalformedURLException
	{
		return String.format("%s%s", FwswaperServlet.SwapServletUrl,
				new String(Base64Coder.encode(url.toString().getBytes())));		
	}
}
