package zsy.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


/**
 * Servlet implementation class UpdateServer
 */
public class UpdateServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateServer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		PrintWriter out = response.getWriter();
		
		// 鍒涘缓鏂囦欢椤圭洰宸ュ巶瀵硅薄  
        DiskFileItemFactory factory = new DiskFileItemFactory();  
  
        // 璁剧疆鏂囦欢涓婁紶璺緞  
        String upload = this.getServletContext().getRealPath("/upload/");  
        // 鑾峰彇绯荤粺榛樿鐨勪复鏃舵枃浠朵繚瀛樿矾寰勶紝璇ヨ矾寰勪负Tomcat鏍圭洰褰曚笅鐨則emp鏂囦欢澶�  
        String temp = System.getProperty("java.io.tmpdir");  
        // 璁剧疆缂撳啿鍖哄ぇ灏忎负 5M  
        factory.setSizeThreshold(1024 * 1024 * 5);  
        // 璁剧疆涓存椂鏂囦欢澶逛负temp  
        factory.setRepository(new File(temp));  
        // 鐢ㄥ伐鍘傚疄渚嬪寲涓婁紶缁勪欢,ServletFileUpload 鐢ㄦ潵瑙ｆ瀽鏂囦欢涓婁紶璇锋眰  
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);  
  
        List<FileItem> list;
        List<String> imageNameList = new ArrayList<String>();
        // 瑙ｆ瀽缁撴灉鏀惧湪List涓�  
        try  
        {  
            list = servletFileUpload.parseRequest(request);  
  
            for (FileItem item : list)  
            {  
                String name = item.getFieldName();  
                InputStream is = item.getInputStream();  
  
                if (name.contains("content"))  
                {  
                    System.out.println(inputStream2String(is));  
                }else if(name.contains("file"))  
                {  
                    try  
                    {  
                        inputStream2File(is, upload + "" + item.getName());  
                        imageNameList.add(upload + "" + item.getName());
                        System.out.println(item.getName());
                    } catch (Exception e)  
                    {  
                        e.printStackTrace();  
                    }  
                }  
            }  
              
           // out.write("success");
           request.setAttribute("imagePathList", imageNameList);
           RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(
        		   "/ImageResultServer");
           
           dispatcher.include(request, response);
            
        } catch (FileUploadException e)  
        {  
            e.printStackTrace();  
            out.write("failure");  
        }  
  
        out.flush();  
        out.close(); 
        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	
	// 娴佽浆鍖栨垚瀛楃涓�  
    public static String inputStream2String(InputStream is) throws IOException  
    {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        int i = -1;  
        while ((i = is.read()) != -1)  
        {  
            baos.write(i);  
        }  
        return baos.toString();  
    }  
  
    // 娴佽浆鍖栨垚鏂囦欢  
    public static void inputStream2File(InputStream is, String savePath)  
            throws Exception  
    {  
        System.out.println("图片存储到 :" + savePath);  
        File file = new File(savePath);  
        InputStream inputSteam = is;  
        BufferedInputStream fis = new BufferedInputStream(inputSteam);  
        FileOutputStream fos = new FileOutputStream(file);  
        int f;  
        while ((f = fis.read()) != -1)  
        {  
            fos.write(f);
        }  
        fos.flush();  
        fos.close();  
        fis.close();  
        inputSteam.close();  
          
    }  
}
