package com.tellhow.wgh;

import com.alibaba.fastjson.JSONObject;
import com.siqiansoft.commons.DateTime;
import com.siqiansoft.commons.DateUtil;
import com.siqiansoft.framework.AppData;
import com.siqiansoft.framework.attach.AttachUtil;
import com.siqiansoft.framework.bo.DatabaseBo;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.List;


public class UploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("upload.action");
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        String userCode= request.getParameter("userCode");
        String deptCode= request.getParameter("deptCode");
        DiskFileItemFactory factory =new DiskFileItemFactory();
        ServletFileUpload upload=new ServletFileUpload(factory);
        try {
            List<FileItem> list=upload.parseRequest(request);
            FileItem file=list.get(0);
            String fileSize= String.valueOf(file.getSize());

            String fileName=file.getName();
            String fileType=fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            String type="FROM";
            String uploadPath = AppData.getInstance().getSystemPath()+"/upload"+ "/" + DateUtil.getCurrentTime("yyyyMM");
            File f = new File(uploadPath);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            String saveFile= AttachUtil.getFileName( fileName);
            String flagId="wghgl";
            InputStream in =file.getInputStream();
            OutputStream out=new FileOutputStream(uploadPath+"/"+saveFile);
            byte[] buffer=new byte[1024];
            while (in.read(buffer)!=-1){
                out.write(buffer);
            }
            out.flush();
            out.close();
            in.close();

            HashMap<String,String> map= new HashMap<String, String>();
            map.put("type",type);
            map.put("flagid",flagId);
            map.put("savepath",uploadPath);
            map.put("savefile",saveFile);
            map.put("sourcefile",fileName);
            map.put("filetype",fileType);
            map.put("filesize",fileSize);
            map.put("usercode",userCode);
            map.put("deptcode",deptCode);
            map.put("encrypt","N");
            map.put("status","N");
            map.put("uptime", DateTime.getNow());
            DatabaseBo dbo = new DatabaseBo();
            String pk="";
            try {
                pk=dbo.insert(map,"EAP_ATTACH");
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject json = new JSONObject();
            json.put("pk",pk);
            json.put("filePath",uploadPath.substring(uploadPath.lastIndexOf("/"))+"/"+saveFile);
            response.getWriter().println(json);
            File oldFile= new File(uploadPath+"/"+saveFile);
            File newFile= new File(uploadPath+"/"+saveFile.substring(0,saveFile.lastIndexOf("."))+"_1"+fileType);
            zipImageFile(oldFile,newFile,80,80,0.3f);


        } catch (FileUploadException e) {
            e.printStackTrace();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    /**
     * 压缩图片
     * @param oldFile  要进行压缩的文件
     * @param newFile  新文件
     * @param width  宽度 //设置宽度时（高度传入0，等比例缩放）
     * @param height 高度 //设置高度时（宽度传入0，等比例缩放）
     * @param quality 质量
     * @return 返回压缩后的文件的全路径
     */
    public static  void  zipImageFile(File oldFile,File newFile, int width, int height,float quality) {
        if (oldFile == null) {
            return ;
        }
        try {
            /** 对服务器上的临时文件进行处理 */
            Image srcFile = ImageIO.read(oldFile);
            int w = srcFile.getWidth(null);
            int h = srcFile.getHeight(null);
            double bili;
            if(width>0){
                bili=width/(double)w;
                height = (int) (h*bili);
            }else{
                if(height>0){
                    bili=height/(double)h;
                    width = (int) (w*bili);
                }
            }

            String srcImgPath = newFile.getAbsoluteFile().toString();
            System.out.println(srcImgPath);
            String subfix = "jpg";
            subfix = srcImgPath.substring(srcImgPath.lastIndexOf(".")+1,srcImgPath.length());

            BufferedImage buffImg = null;

            if(subfix.equals("png")){
                buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }else{
                buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            }
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@测试打印上面buff:"+buffImg);
            Graphics2D graphics = buffImg.createGraphics();

            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@测试打印下面buff:"+buffImg);
            graphics.setBackground(new Color(255,255,255));
            graphics.setColor(new Color(255,255,255));
            graphics.fillRect(0, 0, width, height);
            graphics.drawImage(srcFile.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

            ImageIO.write(buffImg, subfix, new File(srcImgPath));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
