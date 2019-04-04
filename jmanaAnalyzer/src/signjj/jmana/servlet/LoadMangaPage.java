package signjj.jmana.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import signjj.jmana.task.GetMangaData;

/**
 * Servlet implementation class LoadMangaPage
 */
@WebServlet("/LoadMangaPage")
public class LoadMangaPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadMangaPage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");

		String manga_title = request.getParameter("manga_title");
		String manga_vol = request.getParameter("manga_vol");
		String page_base_url = request.getParameter("page_base_url");
		String manga_url = request.getParameter("manga_url");
		System.out.println("Title : "+manga_title+" Vol : "+manga_vol);
		
		GetMangaData gml = new GetMangaData();
		JSONArray dataArr = gml.getMangaPage(manga_title, manga_vol, page_base_url, manga_url);
		
		JSONObject resultJSON = new JSONObject();
		
		if(dataArr != null) {
			resultJSON.put("RESULT", "SUCCESS");
			resultJSON.put("DATA", dataArr);
		}
		else {
			resultJSON.put("RESULT", "FAIL : "+manga_vol);
		}
		
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println(resultJSON);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
