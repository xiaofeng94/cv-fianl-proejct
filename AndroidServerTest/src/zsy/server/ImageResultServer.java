package zsy.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

/**
 * Servlet implementation class ImageResultServer
 */
public class ImageResultServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageResultServer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("wellcome");
		
		PrintWriter writer = response.getWriter();
		
		String resultImagePath = null;
		StringBuffer reqURL = request.getRequestURL();
		int start = 0;
		int end = reqURL.length()-1;
		for(int i = reqURL.length()-1;i > 0;--i){
			if(reqURL.charAt(i) == '/'){
				end = i+1;
				break;
			}
		}
//		System.out.println(reqURL.length()-1);
//		System.out.println(end);
		resultImagePath = reqURL.substring(start, end);
		String resultContent = resultImagePath+"images/";
//		writer.write(resultImagePath+"images/ll1.jpg");
		
//		String updateRoot = (String)request.getAttribute("uploadRoot");
		List<String> cmdarray = (List<String>) request.getAttribute("imagePathList");
		
		String[] cmds = new String[cmdarray.size()+1];
		cmds[0] = "E:\\AndroidImagesProcessor\\Stitch.exe";
		for(int i = 0;i < cmdarray.size();++i){
			cmds[i+1] = cmdarray.get(i);
		}
		
		for(int i = 0;i < cmds.length;++i){
			System.out.println("cmds:"+cmds[i]);
		}
//		System.out.println("cmds:" + cmdarray.size());
//		String[] cmdarray = {"E:\\AndroidImagesProcessor\\JavaInvokTest.exe","success!","21321"};
		
		Process process = java.lang.Runtime.getRuntime().exec(cmds);
		BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String imageName = buf.readLine();

		resultContent = resultContent + imageName;
//		resultContent = resultContent + "result.jpg";
		System.out.println("result:"+resultContent);
		writer.write(resultContent);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request,response);
	}

}
