import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Redirects the context root to {@code /login.do}. Mapped in {@code web.xml} so embedded
 * Cargo/Tomcat always picks it up (annotation-only filters can be skipped in some setups).
 */
public class RootRedirectFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		String uri = req.getRequestURI();
		String ctx = req.getContextPath();
		String afterCtx = uri.startsWith(ctx) ? uri.substring(ctx.length()) : uri;
		if (afterCtx.isEmpty()) {
			afterCtx = "/";
		}
		boolean atRoot = "/".equals(afterCtx);

		if (atRoot) {
			resp.sendRedirect(resp.encodeRedirectURL(ctx + "/login.do"));
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
