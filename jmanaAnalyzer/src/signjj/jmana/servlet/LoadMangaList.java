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

import signjj.jmana.db.DBManager;
import signjj.jmana.task.GetMangaData;

/**
 * Servlet implementation class LoadMangaList
 */
@WebServlet("/LoadMangaList")
public class LoadMangaList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadMangaList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		
		String list_base_url = request.getParameter("list_base_url");
		String manga_title = request.getParameter("manga_title");
		System.out.println(list_base_url+manga_title);
		
		GetMangaData gml = new GetMangaData();
		JSONArray dataArr = gml.getMangaList(list_base_url, manga_title);
		
		DBManager db = new DBManager();
		JSONArray dbArr = null;
		try {
			dbArr = db.select(manga_title);
		} catch (Exception e) {
		}
		
		JSONObject resultJSON = new JSONObject();
		
		if(dataArr != null && dbArr != null) {
			resultJSON.put("RESULT", "SUCCESS");
			resultJSON.put("DATA1", dataArr);
			resultJSON.put("DATA2", dbArr);
		}
		else {
			resultJSON.put("RESULT", "FAIL");
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
