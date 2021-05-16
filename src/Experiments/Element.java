/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Experiments;

import java.util.ArrayList;


public class Element {
    ArrayList<String> paramNames;
    ArrayList<Double> paramValues;

    public Element(ArrayList<String> paramNames, ArrayList<Double> paramValues) {
        this.paramNames = paramNames;
        this.paramValues = paramValues;
    }
    
    public Element() {}

    public ArrayList<String> getParamNames() {
        return paramNames;
    }

    public void setParamNames(ArrayList<String> paramNames) {
        this.paramNames = paramNames;
    }

    public ArrayList<Double> getParamValues() {
        return paramValues;
    }

    public void setParamValues(ArrayList<Double> paramValues) {
        this.paramValues = paramValues;
    }
    
}
