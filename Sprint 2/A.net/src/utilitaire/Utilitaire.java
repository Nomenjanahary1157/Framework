/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilitaire;

import annotation.Url;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Utilitaire {
    public static void sendData(ModelView mv,HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        for (HashMap.Entry<String, Object> hm : mv.getData().entrySet()) {
            request.setAttribute(hm.getKey(), hm.getValue());
        }
        RequestDispatcher dispach = request.getRequestDispatcher(mv.getUrl());
        dispach.forward(request, response);
    }
  public static String getObjet(String uri) throws Exception
    {
        String[] uris = uri.split("\\/");
        String[] s = uris[uris.length-1].split(".do");
        System.out.println(s[0]);
        return s[0];
    }
    public static String[] getNomClass(String[] cls)
    {
        for (int i = 0; i < cls.length; i++) 
        {
            cls[i] = cls[i].split(".class")[0];
        }
        return cls;
    }
    public static HashMap getHMap(String[] files,String pckg) throws ClassNotFoundException
    {
        files = getNomClass(files);
        HashMap hm = new HashMap();
       Class a = Class.forName("annotation.Url");
       
        for (String file : files) 
        {
            Class c = Class.forName(pckg+"."+file);
            Method[] method = c.getDeclaredMethods();
            for (Method m : method)
            {
                if (m.isAnnotationPresent(a))
                {
                    Url url = (Url) m.getAnnotation(a);
                    hm.put(url.value(), c);
                }
            }
        }
        return hm;
    }
    public static ModelView  getList(String url,ServletContext sc)throws Exception
    {
        HashMap<String,Object> hm = (HashMap<String,Object>)sc.getAttribute("hashmap");
        Class functionAnnotation = Class.forName("annotation.Url");
        for (HashMap.Entry <String,Object> v : hm.entrySet())
        {
            Class cl = (Class)v.getValue();
            if (url.equals(v.getKey())) 
            {
                Method[] mtds = cl.getDeclaredMethods();
                for (Method mtd : mtds) 
                {
                    
                    if (mtd.isAnnotationPresent(functionAnnotation))
                    {
                        Url u = (Url)mtd.getAnnotation(functionAnnotation);
                        if (u.value().equals(url)) 
                        {
                        
                        return (ModelView) mtd.invoke(cl.getConstructor().newInstance());
                        }
                        
                    }
                }
            }
        }
        return null;
    }  
}