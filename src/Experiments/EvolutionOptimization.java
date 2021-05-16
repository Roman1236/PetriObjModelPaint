/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Experiments;

import LibTest.Kursach;
import PetriObj.ExceptionInvalidNetStructure;
import PetriObj.ExceptionInvalidTimeDelay;
import PetriObj.PetriObjModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class EvolutionOptimization {
    
    PetriObjModel model;
    public int generationNum;
    public int elementsNum;
    ArrayList<String> params;


    public ArrayList<Double> lLimit;
    public ArrayList<Double> uLimit;  
    public double result;    
    public ArrayList<Double> responseVariable;
    String output = "";

    public String getOutput() {
        return output;
    }
    
    public EvolutionOptimization(ArrayList<Double> lLimit, ArrayList<Double> ULimit, int gNum, int elNum) {
        this.lLimit = lLimit;
        this.uLimit = ULimit;
        this.generationNum = gNum;
        this.elementsNum = elNum;
        params = new ArrayList<>();
        setParams();
    }

    public void optimizeModel() {
        try{
            PetriObjModel m0 = Kursach.getModel();
            m0.setIsProtokol(false);
            m0.go(10000);
            result = responseVariable(m0);           
            
            ArrayList<Element> tempPopulation = generateStartPopulation(elementsNum);
            Element tempEl = new Element();
            for(int i = 0; i < generationNum; i++) {
                tempEl = crossElement(tempPopulation);
                if(i == generationNum-1) {
                    PetriObjModel model = Kursach.getModel();
                    model.setIsProtokol(false);
                    for(int j = 0; j < params.size(); j++) {
                        writePbyName(params.get(j), tempEl.paramValues.get(j), model);
                    }
                    model.go(10000);
                    //System.out.println("start model: "+responseVariable(m0)+"   result: " + responseVariable(model));
                    if(responseVariable(model) < responseVariable(m0)) {
                        output += "succesful optimization\nnew parameter values";  
                        for(int j = 0; j < params.size(); j++) {
                        output += (" "+params.get(j)+" = "+tempEl.paramValues.get(j));
                        }
                    }
                    else {
                        output += "optimization failed";
                    }
                                        
                }
                else {
                tempPopulation = generatePopulation(tempEl);
                }
            }
            
            
        }
        catch(ExceptionInvalidNetStructure e) {
            System.out.println("error1");
        }
        catch(ExceptionInvalidTimeDelay e) {
            System.out.println("error2");
        }       
    }
    
    public ArrayList<Element> generatePopulation(Element e) {
        ArrayList<Element> population = new ArrayList<>();
        ArrayList<Double> values = new ArrayList<>();
        for(int i = 0; i < e.paramValues.size(); i++) {
            values.add(e.paramValues.get(i));
        } 
        for(int j = 0; j < elementsNum; j++) {            
            Element el = new Element(params, values);
            population.add(el);
        }        
        return population;
    }
    
    public Element crossElement(ArrayList<Element> generation) {
        Element el = new Element();
        ArrayList<Double> resVal = new ArrayList<>();
        try{        
            for(int i = 0; i < generation.size(); i++) {
                generation.set(i, changeElement(generation.get(i)));
            }            
            for(int i = 0; i < generation.size(); i++) {
                PetriObjModel m = Kursach.getModel();
                m.setIsProtokol(false);            
                for(int j = 0; j < 3; j++) {
                    writePbyName(params.get(j), generation.get(i).paramValues.get(j), m);
                }
                m.go(10000);
                resVal.add(responseVariable(m));
            }
            
            int i1 = 0;
            int i2 = 0;
            ArrayList<Double> copy = new ArrayList<>();
            for(int i = 0; i < resVal.size(); i++) {
                copy.add(resVal.get(i));
            }
            Collections.sort(copy);
            for(int i = 0; i < resVal.size(); i++) {           
                if(resVal.get(i) == copy.get(0)) {
                    i1 = i;       
                }
                if(resVal.get(i) == copy.get(1)) {
                    i2 = i;
                }           
            }
            el = generateElement(generation.get(i1),generation.get(i2));
        }
        catch(ExceptionInvalidNetStructure e) {
            System.out.println("error1");
        }
        catch(ExceptionInvalidTimeDelay e) {
            System.out.println("error2");
        }             
        return el;
    } 
    
    public Element changeElement(Element e) {        
        Random r = new Random();
        ArrayList<Integer> arr = new ArrayList<>();
        ArrayList<Double> changedValues = new ArrayList<>();
        for(int i = 0; i < params.size(); i++) {
            arr.add(r.nextInt(3));
            if(arr.get(i) == 0) {
                if(e.getParamValues().get(i)+1 <= uLimit.get(i)) {
                    changedValues.add(e.getParamValues().get(i)+1);
                }
                else {
                    changedValues.add(e.getParamValues().get(i));
                }
                
            }
            if(arr.get(i) == 1) {
                if(e.getParamValues().get(i)-1 >= lLimit.get(i)) {
                    changedValues.add(e.getParamValues().get(i)-1);
                }
                else {
                    changedValues.add(e.getParamValues().get(i));
                }
            }
            if(arr.get(i) == 2) {
                changedValues.add(e.getParamValues().get(i));
            }

        }
        e.setParamValues(changedValues);        
        return e;
    }
    
    public Element generateElement(Element e1, Element e2) {
        Random r = new Random();
        ArrayList<Integer> arr = new ArrayList<>();
        for(int i = 0; i < params.size(); i++) {
            arr.add(r.nextInt(2));
        }
        ArrayList<Double> newParams = new ArrayList<>();
        for(int i = 0; i < params.size(); i++) {
            if(arr.get(i) == 0) {
                newParams.add(e1.getParamValues().get(i));
            }
            else {
                newParams.add(e2.getParamValues().get(i));
            }                
        }
        Element e = new Element(params, newParams);
        return e;
    }
    
    public ArrayList<Element> generateStartPopulation(int elNum) {
        ArrayList<Element> population = new ArrayList<>();          
            for(int i = 0; i < elNum; i++) {
                Random r = new Random();            
                double temp1 = r.nextInt((int) (uLimit.get(0) - lLimit.get(0))) + lLimit.get(0);
                double temp2 = r.nextInt((int) (uLimit.get(1) - lLimit.get(1))) + lLimit.get(1);
                double temp3 = r.nextInt((int) (uLimit.get(2) - lLimit.get(2))) + lLimit.get(2);          
                ArrayList<Double> arr = new ArrayList<>();
                arr.add(temp1);
                arr.add(temp2);
                arr.add(temp3);
                Element el = new Element(params, arr);
                population.add(el);
            }          
        return population;
    }
    
    public double responseVariable(PetriObjModel m) {
        double idlingNum = m.getListObj().get(0).getNet().getListP()[7].getMark();
        double servedNum = m.getListObj().get(0).getNet().getListP()[8].getMark();
        double responseVariable = idlingNum / (servedNum + idlingNum);
        return responseVariable;
    }
    
    public void setModel() {
        try{        
            model = Kursach.getModel();
        }
        catch(ExceptionInvalidNetStructure e) {
            System.out.println("error1");
        }
        catch(ExceptionInvalidTimeDelay e) {
            System.out.println("error2");
        }
    }
    
    public void setParams() {
        params.add("T1");
        params.add("T2");
        params.add("T5");       
    }
    
      public double readPname(String fName) {         
        char[] strToArray = fName.toCharArray();
        for(int j = 0; j < model.getListObj().size(); j++) {
            if(strToArray[0] == 'P' || strToArray[0] == 'p') {                       
                for(int i = 0; i < model.getListObj().get(j).getNet().getListP().length; i++) {
                    if(fName.equals(model.getListObj().get(j).getNet().getListP()[i].getName())) {
                        return (double)model.getListObj().get(j).getNet().getListP()[i].getMark(); 
                    }
                }                                                     
            }
            if(strToArray[0] == 'T' || strToArray[0] == 't') {
                for(int i = 0; i < model.getListObj().get(j).getNet().getListT().length; i++) {
                    if(fName.equals(model.getListObj().get(j).getNet().getListT()[i].getName())) {
                        return (double)model.getListObj().get(j).getNet().getListT()[i].getParametr(); 
                    }
                } 
            }
        }
        return -1;    
    }
      
    public void writePbyName(String fName, double value, PetriObjModel model) {
        char[] strToArray = fName.toCharArray();
        for(int j = 0; j < model.getListObj().size(); j++) {
            if(strToArray[0] == 'P' || strToArray[0] == 'p') {                       
                for(int i = 0; i < model.getListObj().get(j).getNet().getListP().length; i++) {
                    if(fName.equals(model.getListObj().get(j).getNet().getListP()[i].getName())) {
                        model.getListObj().get(j).getNet().getListP()[i].setMark((int)value);
                    }
                }                                                   
            }
            if(strToArray[0] == 'T' || strToArray[0] == 't') {
                for(int i = 0; i < model.getListObj().get(j).getNet().getListT().length; i++) {
                    if(fName.equals(model.getListObj().get(j).getNet().getListT()[i].getName())) {
                        model.getListObj().get(j).getNet().getListT()[i].setParametr(value);
                    }
                } 
            }
        }
    }
}
