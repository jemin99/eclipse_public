package signjj.jmana.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import signjj.jmana.util.FileControl;

/**
 * Servlet implementation class FileDownload
 */
@WebServlet("/FileDownload")
public class FileDownload extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileDownload() {
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
		
		String root = request.getParameter("root");
		String title = request.getParameter("title");
		String vol = request.getParameter("vol");
		String fileName = request.getParameter("fileName");
		String url = request.getParameter("url");
		
		FileControl fc = new FileControl();
		
		JSONObject resultJSON = new JSONObject();
		
		String titleFolder = root + File.separator + title;
		String volFolder = root + File.separator + title + File.separator + vol;
		//String titleFolder = "E:\\JMANA" + File.separator + title;
		//String volFolder = "E:\\JMANA" + File.separator + title + File.separator + vol;
		
		fc.createFolder(titleFolder);
		fc.createFolder(volFolder);
		
		String fullFileName = volFolder + File.separator + fileName;
		
		boolean result = fc.download(url, fullFileName);
		
		if(result) {
			resultJSON.put("RESULT", "SUCCESS");
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
