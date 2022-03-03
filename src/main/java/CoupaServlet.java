import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CoupaServlet
 */
@WebServlet("/CoupaServlet")
public class CoupaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final String HOST = "93.148.193.197";
	private final String PASS = "C0up42022!";
	private final int PORT = 21;
	private final String USER = "Coupa";

	/**
	 * Default constructor.
	 */
	public CoupaServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at get: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		ServletInputStream inputStream = request.getInputStream();
		String input = CoupaUtils.xmlParser(inputStream);
		boolean saved = CoupaUtils.saveFileFTP(input, HOST, PORT, USER, PASS);
		if(saved) {
			response.getWriter().append(CoupaUtils.responseOK()).append(request.getContextPath());
		} else {
			response.getWriter().append(CoupaUtils.responseKO()).append(request.getContextPath());
		}
	}
}
