/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilitaire;

import annotation.Url;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
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
    public static ModelView  getList(String url,ServletContext sc,HttpServletRequest request)throws Exception
    {
        HashMap<String,Object> hm = (HashMap<String,Object>)sc.getAttribute("hashmap");
        Class functionAnnotation = Class.forName("annotation.Url");
        Enumeration name = request.getParameterNames();
        boolean insertion = false;
        
        ArrayList<String> al = new ArrayList<String>();
        while (name.hasMoreElements()) 
        {
            al.add((String) name.nextElement());            
            insertion = true;
        }
        for (HashMap.Entry <String,Object> v : hm.entrySet())
        {
            Class cl = (Class)v.getValue();
            Object obj = cl.getConstructor().newInstance();
            if (url.equals(v.getKey())) 
            {
                if (insertion)
                {
                    
                    Field[] attributs = cl.getDeclaredFields();
                    for (int i = 0; i < attributs.length; i++)
                    {
                        if (attributs[i].getName().equals(al.get(i)))
                        {
                            System.out.println(al.get(i));
                            System.out.println("set"+attributs[i].getName().substring(0,1).toUpperCase()+attributs[i].getName().substring(1));
                            Method m = cl.getDeclaredMethod("set"+attributs[i].getName().substring(0,1).toUpperCase()+attributs[i].getName().substring(1),attributs[i].getType());
                            
                            if(getType(attributs[i]).equals("Date"))
                            {
                                m.invoke(obj, new Date(request.getParameter(al.get(i))));
                            }
                            else if(getType(attributs[i]).equals("Int"))
                            {
                                m.invoke(obj, Integer.parseInt(request.getParameter(al.get(i))));
                            }
                            else if(getType(attributs[i]).equals("Double"))
                            {
                                m.invoke(obj, Double.parseDouble(request.getParameter(al.get(i))));
                                
                            }
                            else
                            {
                                
                                m.invoke(obj, request.getParameter(al.get(i)));
                            }
                                                            
                        }
                    }
                    ModelView mvv = new ModelView();
                    HashMap<String,Object> hms = new HashMap<String,Object>();
                    hms.put("objet", obj);
                    mvv.setHm(hms);
                    return mvv;
                }
                else
                {
                    Method[] mtds = cl.getDeclaredMethods();
                    for (Method mtd : mtds)
                    {
                        if (mtd.isAnnotationPresent(functionAnnotation))
                        {
                            Url u = (Url)mtd.getAnnotation(functionAnnotation);
                            if (u.value().equals(url)) 
                            {  
                                return (ModelView) mtd.invoke(obj);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }  
    public static String getType(Field attr)
    {
        if (attr.getType().getSimpleName().equals("String")) 
        {
            return "String";
        }
        else if (attr.getType().getSimpleName().equals("Date")) 
        {
            return "Date";
        }
        else if(attr.getType().getSimpleName().equals("Integer") || attr.getType().getSimpleName().equals("int"))
        {
            return "Int";
        }
        else
        {
            return "Double";
        }
    }
}