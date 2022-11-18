/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilitaire;

import java.util.HashMap;

public class ModelView {
    public String url;
    public HashMap<String,Object> hm;

    public String getUrl() {
        return url;
    }
    public ModelView()
    {
        this.hm = new HashMap<String,Object>();
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, Object> getData() {
        return hm;
    }

    public void setHm(HashMap<String, Object> hm) {
        this.hm = hm;
    }
}