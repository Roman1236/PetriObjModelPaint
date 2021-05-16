/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LibTest;

import LibNet.NetLibrary;
import PetriObj.ArcIn;
import PetriObj.ArcOut;
import PetriObj.ExceptionInvalidNetStructure;
import PetriObj.ExceptionInvalidTimeDelay;
import PetriObj.PetriNet;
import PetriObj.PetriObjModel;
import PetriObj.PetriP;
import PetriObj.PetriSim;
import PetriObj.PetriT;
import Experiments.FactExp;

import java.util.ArrayList;


public class Kursach{      
    public static PetriObjModel m;
    
      public static void main(String[] args) throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure { 
        for(int i = 0; i < 10; i++)
          GoModel();
          //FactExp f = new FactExp();
          //f.regressionAnalysis(5); 
          //f.exp(Kursach.getModel(),2,0.95,2,1,"full","P1","norm");
          
//        ArrayList<ArrayList<Double>> array = new ArrayList<>();
//        ArrayList<Integer> ar = new ArrayList<>();
//        int x = 10;
//        int x_min = 5;
//        int x_max = 20;
//        int x_step = 5;
//        ar.add(x);
//        ar.add(x_min);
//        ar.add(x_max);
//        ar.add(x_step);
////        FactExp f = new FactExp();
////        f.exp(getModel(10,10), 1, 0.95, 1, 1, array, "full", "P9", "norm");
//        //
//GoModel();   

      }    

      public static void setModel() throws ExceptionInvalidNetStructure, ExceptionInvalidTimeDelay {
          try{        
          PetriObjModel model = getModel();
          model.setIsProtokol(false);       
          m = model;}
          catch(ExceptionInvalidNetStructure e) {
              System.out.println("error");
          }
          catch(ExceptionInvalidTimeDelay e) {
          }
 
      }    
      
      public static void GoModel() throws ExceptionInvalidNetStructure, ExceptionInvalidTimeDelay {
          //for(int i = 0; i < 10; i++) {
          PetriObjModel model = getModel();
          model.setIsProtokol(false);       
          m = model;
          double timeModeling = 1000000;          
          
          //model.go(timeModeling*i);      
          model.go(timeModeling);
          double idlingNum = model.getListObj().get(0).getNet().getListP()[7].getMark();
          double servedNum = model.getListObj().get(0).getNet().getListP()[8].getMark();
          double idlingProbability = idlingNum / (servedNum + idlingNum);
          System.out.println("idling probability: "+idlingProbability);
             
          double meanQ1 = model.getListObj().get(0).getNet().getListP()[1].getMean();
          double meanQ2 = model.getListObj().get(0).getNet().getListP()[3].getMean();
          double maxQ1 = model.getListObj().get(0).getNet().getListP()[1].getObservedMax();
          double maxQ2 = model.getListObj().get(0).getNet().getListP()[3].getObservedMax();
          System.out.println("served: "+servedNum);
//          System.out.println("mean queue №1: "+meanQ1);
//          System.out.println("mean queue №2: "+meanQ2);
//          System.out.println("max queue №1: "+maxQ1);
//          System.out.println("max queue №2: "+maxQ2);   
          //}
       
      }
      
   
public static PetriNet CreateNetfinal() throws ExceptionInvalidNetStructure, ExceptionInvalidTimeDelay {
	ArrayList<PetriP> d_P = new ArrayList<>();
	ArrayList<PetriT> d_T = new ArrayList<>();
	ArrayList<ArcIn> d_In = new ArrayList<>();
	ArrayList<ArcOut> d_Out = new ArrayList<>();
	d_P.add(new PetriP("P1",1));
	d_P.add(new PetriP("P2",0));
	d_P.add(new PetriP("P3",1));
	d_P.add(new PetriP("P4",0));
	d_P.add(new PetriP("P5",0));
	d_P.add(new PetriP("P6",1));
	d_P.add(new PetriP("P7",0));
	d_P.add(new PetriP("P8",0));
	d_P.add(new PetriP("P9",0));
	d_P.add(new PetriP("P10",1));
	d_T.add(new PetriT("T1",10.0));
	d_T.get(0).setDistribution("norm", d_T.get(0).getTimeServ());
	d_T.get(0).setParamDeviation(1.0);
	d_T.add(new PetriT("T2",40.0));
	d_T.get(1).setDistribution("norm", d_T.get(1).getTimeServ());
	d_T.get(1).setParamDeviation(1.0);
	d_T.add(new PetriT("T3",0.0));
	d_T.add(new PetriT("T5",10));
	d_T.get(3).setDistribution("exp", d_T.get(3).getTimeServ());
	d_T.get(3).setParamDeviation(0.0);
	d_T.add(new PetriT("T4",0.0));
        d_T.get(4).setPriority(1);
	d_T.add(new PetriT("T6",0.0));
	d_In.add(new ArcIn(d_P.get(0),d_T.get(0),1));
	d_In.add(new ArcIn(d_P.get(2),d_T.get(1),1));
	d_In.add(new ArcIn(d_P.get(1),d_T.get(2),10));
	d_In.add(new ArcIn(d_P.get(3),d_T.get(2),10));
	d_In.add(new ArcIn(d_P.get(5),d_T.get(3),1));
	d_In.add(new ArcIn(d_P.get(4),d_T.get(4),1));
	d_In.add(new ArcIn(d_P.get(6),d_T.get(4),1));
	d_In.add(new ArcIn(d_P.get(6),d_T.get(5),1));
	d_In.add(new ArcIn(d_P.get(9),d_T.get(2),1));
	d_Out.add(new ArcOut(d_T.get(0),d_P.get(0),1));
	d_Out.add(new ArcOut(d_T.get(0),d_P.get(1),5));
	d_Out.add(new ArcOut(d_T.get(1),d_P.get(2),1));
	d_Out.add(new ArcOut(d_T.get(1),d_P.get(3),20));
	d_Out.add(new ArcOut(d_T.get(2),d_P.get(4),1));
	d_Out.add(new ArcOut(d_T.get(3),d_P.get(5),1));
	d_Out.add(new ArcOut(d_T.get(3),d_P.get(6),1));
	d_Out.add(new ArcOut(d_T.get(5),d_P.get(7),1));
	d_Out.add(new ArcOut(d_T.get(4),d_P.get(8),1));
	d_Out.add(new ArcOut(d_T.get(4),d_P.get(9),1));
	PetriNet d_Net = new PetriNet("final",d_P,d_T,d_In,d_Out);
	PetriP.initNext();
	PetriT.initNext();
	ArcIn.initNext();
	ArcOut.initNext();

	return d_Net;
}    


    public static PetriObjModel getModel() throws ExceptionInvalidNetStructure, ExceptionInvalidTimeDelay { 
        ArrayList<PetriSim> list;
        list = new ArrayList<PetriSim>();
        try{       
        list.add(new PetriSim(CreateNetfinal()));                        
        }
        catch(ExceptionInvalidNetStructure e) {
            System.out.println("setmodel error1");
        }
        catch(ExceptionInvalidTimeDelay e) {
            System.out.println("setmodel error2");
        }
        return new PetriObjModel(list);
    }
    
    
           
}
