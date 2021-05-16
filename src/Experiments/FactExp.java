/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Experiments;

import LibNet.NetLibrary;
import LibTest.Kursach;
import PetriObj.ExceptionInvalidNetStructure;
import PetriObj.ExceptionInvalidTimeDelay;
import PetriObj.PetriObjModel;
import PetriObj.PetriSim;


import java.util.ArrayList;


public class FactExp {
    int n;
    int factorNum;
    String textOutputField = "";
    ArrayList<String> fNameList = new ArrayList<>(); 
    ArrayList<Double> fValueList = new ArrayList<>();
    ArrayList<Double> lLimitList = new ArrayList<>();
    ArrayList<Double> uLimitList = new ArrayList<>();
    ArrayList<Double> stepChangeList = new ArrayList<>();
    ArrayList<Double> resultList = new ArrayList<>();    
    ArrayList<ArrayList<Double>> regressionStat = new ArrayList<>();    
    int runNum;
    PetriObjModel model;
    double beta;
    double sigma;
    double epsilon;
    String plan;
    String responseVariable;
    String distribution;

    public String getTextOutputField() {
        return textOutputField;
    }

    public void setTextOutputField(String textOutputField) {
        this.textOutputField = textOutputField;
    }
    
    public ArrayList<String> getfNameList() {
        return fNameList;
    }

    public void setfNameList(ArrayList<String> fNameList) {
        this.fNameList = fNameList;
    }

    public ArrayList<Double> getfValueList() {
        return fValueList;
    }

    public void setfValueList(ArrayList<Double> fValueList) {
        this.fValueList = fValueList;
    }

    public ArrayList<Double> getlLimitList() {
        return lLimitList;
    }

    public void setlLimitList(ArrayList<Double> lLimitList) {
        this.lLimitList = lLimitList;
    }

    public ArrayList<Double> getuLimitList() {
        return uLimitList;
    }

    public void setuLimitList(ArrayList<Double> uLimitList) {
        this.uLimitList = uLimitList;
    }

    public ArrayList<Double> getStepChangeList() {
        return stepChangeList;
    }

    public void setStepChangeList(ArrayList<Double> stepChangeList) {
        this.stepChangeList = stepChangeList;
    }


    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getFactorNum() {
        return factorNum;
    }

    public void setFactorNum(int factorNum) {
        this.factorNum = factorNum;
    }



    public int getRunNum() {
        return runNum;
    }

    public void setRunNum(int runNum) {
        this.runNum = runNum;
    }

    public PetriObjModel getModel() {
        return model;
    }

    public void setModel(PetriObjModel model) {
        this.model = model;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getResponseVariable() {
        return responseVariable;
    }

    public void setResponseVariable(String responseVariable) {
        this.responseVariable = responseVariable;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }
           

    public void exp(PetriObjModel model, int factorNum, double beta, double sigma, double epsilon, String plan, String responseVariable, String distribution ) {
        
        try{        
        PetriObjModel testModel = Kursach.getModel();
        if(plan != "full") {factorNum--;}
        runNum = findRunNum(distribution,sigma, epsilon, beta);
        textOutputField += "number of runs = " + runNum + "\n";
        System.out.println("number of runs: "+runNum);
        double time = findTransitionPeriodTime(testModel);
        textOutputField += "transition period time = " + time + "\n\n";
        System.out.println("transition period time "+ time);
        int arr[][] = regressionAnalysis(factorNum); 

        regressionExp(runNum, time, factorNum, plan, model, arr); 
        
        
        dispersionAnalysis(factorNum, runNum, time);
        
        }
        catch(ExceptionInvalidNetStructure e) {
            System.out.println("error1");
        }
        catch(ExceptionInvalidTimeDelay e) {
            System.out.println("error2");
        }     
           
    }
    
    public double responseVariable(PetriObjModel m) {
        double idlingNum = m.getListObj().get(0).getNet().getListP()[7].getMark();
        double servedNum = m.getListObj().get(0).getNet().getListP()[8].getMark();
        double responseVariable= idlingNum / (servedNum + idlingNum);
        return responseVariable;
    }
    
    public void dispersionAnalysis(int factorNum, int runNum, double timeModeling) {
        ArrayList<ArrayList<Double>> dispResults = new ArrayList<>();
        ArrayList<Double> dispRes0 = new ArrayList<>();
        ArrayList<Double> comparedValues = new ArrayList<>();
        
        try{  
            
            for(int i = 0; i < runNum; i++) {
                PetriObjModel testModel0 = Kursach.getModel();
            
                for(int j = 0; j < factorNum; j ++) {
                    writeFbyName(fNameList.get(j), lLimitList.get(j), testModel0);
                }
                
                testModel0.setIsProtokol(false);
                testModel0.go(timeModeling);
                dispRes0.add(responseVariable(testModel0));
            }
            
            
            for(int i = 0; i < factorNum; i++) {
                
                ArrayList<Double> dispRes = new ArrayList<>();
                
                for(int n = 0; n < runNum; n++) {
                    PetriObjModel testModel = Kursach.getModel();
                    
                    for(int j = 0; j < factorNum; j++) {                
                        if(j == i) {
                            writeFbyName(fNameList.get(j), uLimitList.get(j), testModel);
                        }
                        else{
                            writeFbyName(fNameList.get(j), lLimitList.get(j), testModel);
                        }
                    }
                    
                    testModel.setIsProtokol(false);
                    testModel.go(timeModeling);

                    dispRes.add(responseVariable(testModel));                                                   
                }
                dispResults.add(dispRes);                
            }

            
            double meanValue = 0;
            for(int i = 0; i < dispRes0.size(); i++) {
                meanValue += dispRes0.get(i);
            }
            meanValue /= runNum;

            
            ArrayList<Double> meanValues = new ArrayList<>();
            
            for(int i = 0; i < dispResults.size(); i++) {
                double temp = 0;
                for(int j = 0; j < dispResults.get(i).size(); j++) {
                    temp += dispResults.get(i).get(j);
                }
                meanValues.add(temp/runNum);
            }           

            
            for(int i = 0; i < factorNum; i++) {
                comparedValues.add((meanValues.get(i)+meanValue)/2);
            }          

                      
            ArrayList<Double> Sgen = new ArrayList<>();
            ArrayList<Double> Sfact = new ArrayList<>();
            ArrayList<Double> Sleft = new ArrayList<>();
            ArrayList<Double> Sgen0 = new ArrayList<>();            

            
            for(int j = 0; j < factorNum; j++) {
                double temp = 0;
                for(int i = 0; i < runNum; i++) {
                    temp += Math.pow(dispRes0.get(i) - comparedValues.get(j), 2);
                }
                Sgen0.add(temp);
            }

            
            for(int i = 0; i < factorNum; i++) {
                double temp = 0;
                for(int j = 0; j < runNum; j++) {
                    temp += Math.pow(dispResults.get(i).get(j) - comparedValues.get(i), 2);
                }
                Sgen.add(temp+Sgen0.get(i));
            }

            
            for(int i = 0; i < factorNum; i++) {
                Sfact.add(runNum * ( Math.pow(meanValue - comparedValues.get(i), 2) + Math.pow(meanValues.get(i) - comparedValues.get(i), 2) ) );
            }


            for(int j = 0; j < factorNum; j++) {
                double temp1 = 0;
                double temp2 = 0;
                for(int i = 0; i < runNum; i++) {
                    temp1 += Math.pow(dispRes0.get(i) - meanValue, 2);
                }
                for(int i = 0; i < runNum; i++) {
                    temp2 += Math.pow(dispResults.get(j).get(i) - meanValues.get(j), 2);
                }
                Sleft.add(temp1+temp2);
            }

            
            ArrayList<Double> dgen = new ArrayList<>();
            for(int i = 0; i < factorNum; i++) {
                dgen.add(Sgen.get(i) / ((2 * runNum) -1));
            } 
            
            ArrayList<Double> dfact = new ArrayList<>();
            for(int i = 0; i < factorNum; i++) {
                dfact.add(Sfact.get(i));
            }             
            
            ArrayList<Double> dleft = new ArrayList<>();
            for(int i = 0; i < factorNum; i++) {
                dleft.add(Sleft.get(i) / 2*(runNum - 1));
            }                     
            
            int k1 = 1;
            int k2 = 2*(runNum -1);
            
            double Ftable = Ftabl().get(k2-1);            
            
            ArrayList<Double> F = new ArrayList<>();
            for(int i = 0; i < factorNum; i++) {
                F.add(dfact.get(i) / dleft.get(i));
                System.out.println("F"+i+" = " + F.get(i));
            } 
            
            textOutputField += "\n Dispersion analysis\nFisher criterion:\n\nFtable = "+Ftable+"\n";
            
            for(int i = 0; i < factorNum; i++) {
                textOutputField += "Fcr"+(i+1)+" = "+String.format("%.5f",F.get(i))+"  ";
            }
            
        }
        catch(ExceptionInvalidNetStructure e) {
            System.out.println("error1");
        }
        catch(ExceptionInvalidTimeDelay e) {
            System.out.println("error2");
        }
        
    }
    
    public ArrayList<Double> Ftabl() {
        ArrayList<Double> F = new ArrayList<>();
        F.add(161.45);
        F.add(18.51);
        F.add(10.13);
        F.add(7.71);
        F.add(6.61);
        F.add(5.99);
        F.add(5.59);
        F.add(5.32);
        F.add(5.12);
        F.add(4.96);
        F.add(4.84);
        F.add(4.75);
        F.add(4.67);
        F.add(4.6);
        F.add(4.54);
        F.add(4.49);
        F.add(4.45);
        F.add(4.41);
        F.add(4.38);
        F.add(4.35);
        return F;
    }    
    
    public void regressionExp(int runNum, double timeModeling, int factorNum, String plan, PetriObjModel model, int[][] arr) {        
        
        try{    
            
        ArrayList<Double> results = new ArrayList<>();                       
        
        for(int i = 0; i < arr.length; i++) {               
            
            double temp = 0;  
            ArrayList<Double> tempArr = new ArrayList<>();
            
            for(int k = 0; k < runNum; k++) {  

                PetriObjModel m = Kursach.getModel();                
                m.setIsProtokol(false);                                      
            

 
            for(int j=1;j<=factorNum;j++){
                if(arr[i][j]==1){
                    writeFbyName(fNameList.get(j-1),uLimitList.get(j-1),m);
                }
                else{
                    writeFbyName(fNameList.get(j-1),lLimitList.get(j-1),m);
                }
            }
        
                       
                m.go(timeModeling);                     
               
                tempArr.add(responseVariable(m));
                temp += responseVariable(m) / runNum;
            }
            regressionStat.add(tempArr);
            results.add(temp);                          
        }
        
        
        ArrayList<Double> k = new ArrayList<>();
        
        for(int i = 0; i < (int)Math.pow(2, factorNum); i++) {
            double sum = 0;
            for(int j = 0; j < (int)Math.pow(2, factorNum); j++) {
                sum += results.get(j)*arr[j][i];
                System.out.println("j "+j+"  i "+i+"  = "+results.get(j)*arr[j][i]);
            }
            k.add(sum/runNum);            
        }
        
        
        textOutputField += "Regression equation\n";
        String plus = "+";
        if(factorNum == 2) {            
            textOutputField += String.format("%.5f",k.get(0))+"";
            if(k.get(1) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(1))+"x1";
            if(k.get(2) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(2))+"x2";
            if(k.get(3) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(3))+"x1x2";                
        }
        if(factorNum == 3) {
            textOutputField += String.format("%.5f",k.get(0))+"";
            if(k.get(1) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(1))+"x1";
            if(k.get(2) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(2))+"x2";
            if(k.get(3) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(3))+"x3";
            if(k.get(4) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(4))+"x1x2";
            if(k.get(5) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(5))+"x1x3";
            if(k.get(6) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(6))+"x2x3";
            if(k.get(7) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(7))+"x1x2x3";
        }
        if(factorNum == 4) {
            textOutputField += String.format("%.5f",k.get(0))+"";
            if(k.get(1) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(1))+"x1";
            if(k.get(2) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(2))+"x2";
            if(k.get(3) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(3))+"x3";
            if(k.get(4) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(4))+"x4";
            if(k.get(5) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(5))+"x1x2";
            if(k.get(6) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(6))+"x1x3";
            if(k.get(7) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(7))+"x1x4";
            if(k.get(8) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(8))+"x2x3";
            if(k.get(9) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(9))+"x2x4";
            if(k.get(10) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(10))+"x3x4";
            if(k.get(11) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(11))+"x1x2x3";
            if(k.get(12) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(12))+"x1x2x4";
            if(k.get(13) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(13))+"x1x3x4";
            if(k.get(14) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(14))+"x2x3x4";
            if(k.get(15) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(15))+"x1x2x3x4";                        
        }
        if(factorNum == 5) {
            textOutputField += String.format("%.5f",k.get(0))+"";
            if(k.get(1) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(1))+"x1";
            if(k.get(2) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(2))+"x2";
            if(k.get(3) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(3))+"x3";
            if(k.get(4) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(4))+"x4";
            if(k.get(5) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(5))+"x5";
            if(k.get(6) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(6))+"x1x2";
            if(k.get(7) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(7))+"x1x3";
            if(k.get(8) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(8))+"x1x4";
            if(k.get(9) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(9))+"x1x5";
            if(k.get(10) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(10))+"x2x3";
            if(k.get(11) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(11))+"x2x4";
            if(k.get(12) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(12))+"x2x5";
            if(k.get(13) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(13))+"x3x4";
            if(k.get(14) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(14))+"x3x5";
            if(k.get(15) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(15))+"x4x5";
            if(k.get(16) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(16))+"x1x2x3";
            if(k.get(17) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(17))+"x1x2x4";
            if(k.get(18) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(18))+"x1x2x5";
            if(k.get(19) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(19))+"x1x3x4";
            if(k.get(20) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(20))+"x1x3x5";
            if(k.get(21) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(21))+"x1x4x5";
            if(k.get(22) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(22))+"x2x3x4";
            if(k.get(23) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(23))+"x2x3x5";
            if(k.get(24) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(24))+"x2x4x5";
            if(k.get(25) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(25))+"x3x4x5";
            if(k.get(26) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(26))+"x1x2x3x4";
            if(k.get(27) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(27))+"x1x2x3x5";
            if(k.get(28) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(28))+"x1x2x4x5";
            if(k.get(29) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(29))+"x1x3x4x5";
            if(k.get(30) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(30))+"x2x3x4x5";
            if(k.get(31) >= 0) {textOutputField += plus;}
            textOutputField += String.format("%.5f",k.get(31))+"x1x2x3x4x5";  
        }
        textOutputField += "\n\n";
        double dispersionSum = 0;        
        ArrayList<Double> Dj = new ArrayList<>();
        
        for(int i = 0; i < results.size(); i++) {
            double dsum = 0;
            for(int j = 0; j < runNum; j++) {
                dsum += Math.pow(regressionStat.get(i).get(j)-results.get(i), 2);
            }
            Dj.add(dsum/(runNum-1));            
        }
        
        double maxD = 0;
        for(int i = 0; i < Dj.size(); i++) {
            if(Dj.get(i) > maxD) {
                maxD = Dj.get(i);
            }
            dispersionSum += Dj.get(i);
        }
          
        double G = maxD / dispersionSum;
        
        textOutputField += "Cochren's G-test\n"+"G = "+String.format("%.5f",G);
        textOutputField += "   Gc = "+String.format("%.5f",cochrensG(runNum,results.size()))+"\n";
        
        double D = dispersionSum / results.size();
        textOutputField += "D = "+String.format("%.5f",D)+"\n";
        
        if(G < cochrensG(runNum,results.size())) {
            ArrayList<Double> tj = new ArrayList<>();
            int m = results.size() * (runNum-1);
            double tkr = 0;
            if(m >=1 && m <= 30) {
                tkr = studentTable().get(m-1);
            }
            if(m > 30 && m <= 40) {
                tkr = studentTable().get(30);
            }
            if(m > 40 && m <= 60) {
                tkr = studentTable().get(31);
            }
            if(m > 60 && m <= 120) {
                tkr = studentTable().get(32);
            }
            if(m > 120) {
                tkr = studentTable().get(33);
            }
            
            ArrayList<Integer> significantK = new ArrayList<>();
            
            for(int i = 0; i < k.size(); i++) {
                tj.add(Math.abs(k.get(i)) * Math.sqrt(results.size()*runNum/D));
                
                if(tj.get(i) > tkr) {
                    significantK.add(i);
                }
            } 
            textOutputField += "\nStudent criterion\nStable = "+tkr+"\n";
            for(int i = 0; i < tj.size(); i++) {
                textOutputField += "Scr"+(i+1)+" = "+String.format("%.5f",tj.get(i))+"  ";
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

    public ArrayList<Double> studentTable() {
        ArrayList<Double> stud = new ArrayList<>();
        stud.add(6.314);
        stud.add(2.920);
        stud.add(2.353);
        stud.add(2.132);
        stud.add(2.015);
        
        stud.add(1.943);
        stud.add(1.895);
        stud.add(1.860);
        stud.add(1.833);
        stud.add(1.812);
        
        stud.add(1.796);
        stud.add(1.782);
        stud.add(1.771);
        stud.add(1.761);
        stud.add(1.753);
        
        stud.add(1.746);
        stud.add(1.740);
        stud.add(1.734);
        stud.add(1.729);
        stud.add(1.725);
        
        stud.add(1.721);
        stud.add(1.717);
        stud.add(1.714);
        stud.add(1.711);
        stud.add(1.708);
        
        stud.add(1.706);
        stud.add(1.703);
        stud.add(1.701);
        stud.add(1.699);
        stud.add(1.697);
        
        stud.add(1.684);
        stud.add(1.671);
        stud.add(1.658);
        stud.add(1.645);
        
        return stud;
    }
    public double cochrensG(int runNum, int N) {
        double Gc = 0;
        int m = runNum-1;
        int[] arr1 = {2,3,4,5,6,7,8,9,10,15,20,30,40,60,120,(int)Double.POSITIVE_INFINITY};
        int[] arr2 = {1,3,6,10,16,36,144,(int)Double.POSITIVE_INFINITY};
        int c1=0;
        int c2=0;
        
        for(int i = 1; i < arr1.length; i++) {
            if(m < arr1[i]) {
                c1 = i-1;
                break;
            }            
        }
        
        for(int i = 1; i < arr2.length; i++) {
            if(N < arr2[i]) {
                c2 = i-1;
                break;
            }            
        }
        
        Gc = cohrensTable().get(c1).get(c2);
        
        return Gc;
    }
    
    public static ArrayList<ArrayList<Double>> cohrensTable() {
        ArrayList<ArrayList<Double>> arr = new ArrayList<>();
        ArrayList<Double> arr1 = new ArrayList<>();
        ArrayList<Double> arr2 = new ArrayList<>();
        ArrayList<Double> arr3 = new ArrayList<>();
        ArrayList<Double> arr4 = new ArrayList<>();
        ArrayList<Double> arr5 = new ArrayList<>();
        ArrayList<Double> arr6 = new ArrayList<>();
        ArrayList<Double> arr7 = new ArrayList<>();
        ArrayList<Double> arr8 = new ArrayList<>();
        ArrayList<Double> arr9 = new ArrayList<>();
        ArrayList<Double> arr10 = new ArrayList<>();
        ArrayList<Double> arr11 = new ArrayList<>();
        ArrayList<Double> arr12 = new ArrayList<>();
        ArrayList<Double> arr13 = new ArrayList<>();
        ArrayList<Double> arr14 = new ArrayList<>();
        ArrayList<Double> arr15 = new ArrayList<>();
        ArrayList<Double> arr16 = new ArrayList<>();
        arr.add(arr1);
        arr.add(arr2);
        arr.add(arr3);
        arr.add(arr4);
        arr.add(arr5);
        arr.add(arr6);
        arr.add(arr7);
        arr.add(arr8);
        arr.add(arr9);
        arr.add(arr10);
        arr.add(arr11);
        arr.add(arr12);
        arr.add(arr13);
        arr.add(arr14);
        arr.add(arr15);
        arr.add(arr16);
        arr1.add(0.9985); arr1.add(0.9392); arr1.add(0.8534); arr1.add(0.7880); arr1.add(0.7341); arr1.add(0.6602); arr1.add(0.5813); arr1.add(0.5); 
        arr2.add(0.9669); arr2.add(0.7977); arr2.add(0.6771); arr2.add(0.6025); arr2.add(0.5466); arr2.add(0.4748); arr2.add(0.4031); arr2.add(0.3333); 
        arr3.add(0.9065); arr3.add(0.6841); arr3.add(0.5598); arr3.add(0.4884); arr3.add(0.4366); arr3.add(0.3720); arr3.add(0.3093); arr3.add(0.25); 
        arr4.add(0.8412); arr4.add(0.5981); arr4.add(0.4783); arr4.add(0.4118); arr4.add(0.3645); arr4.add(0.3066); arr4.add(0.2513); arr4.add(0.2); 
        arr5.add(0.7808); arr5.add(0.5321); arr5.add(0.4184); arr5.add(0.3568); arr5.add(0.3135); arr5.add(0.2612); arr5.add(0.2129); arr5.add(0.1667); 
        arr6.add(0.7271); arr6.add(0.4800); arr6.add(0.3726); arr6.add(0.3154); arr6.add(0.2756); arr6.add(0.2278); arr6.add(0.1833); arr6.add(0.1429); 
        arr7.add(0.6798); arr7.add(0.4377); arr7.add(0.3362); arr7.add(0.2829); arr7.add(0.2462); arr7.add(0.2022); arr7.add(0.1616); arr7.add(0.1250); 
        arr8.add(0.6385); arr8.add(0.4027); arr8.add(0.3067); arr8.add(0.2568); arr8.add(0.2226); arr8.add(0.1820); arr8.add(0.1446); arr8.add(0.1111); 
        arr9.add(0.6020); arr9.add(0.3733); arr9.add(0.2823); arr9.add(0.2353); arr9.add(0.2032); arr9.add(0.1655); arr9.add(0.1308); arr9.add(0.1); 
        arr10.add(0.4709); arr10.add(0.2758); arr10.add(0.2034); arr10.add(0.1671); arr10.add(0.1429); arr10.add(0.1144); arr10.add(0.0889); arr10.add(0.0667); 
        arr11.add(0.3894); arr11.add(0.2205); arr11.add(0.1602); arr11.add(0.1303); arr11.add(0.1108); arr11.add(0.0879); arr11.add(0.0675); arr11.add(0.05); 
        arr12.add(0.2929); arr12.add(0.1593); arr12.add(0.1137); arr12.add(0.0921); arr12.add(0.0771); arr12.add(0.0604); arr12.add(0.0457); arr12.add(0.0333); 
        arr13.add(0.2370); arr13.add(0.1259); arr13.add(0.0887); arr13.add(0.0713); arr13.add(0.0595); arr13.add(0.0462); arr13.add(0.0347); arr13.add(0.0250); 
        arr14.add(0.1737); arr14.add(0.0895); arr14.add(0.0623); arr14.add(0.0497); arr14.add(0.0411); arr14.add(0.0316); arr14.add(0.0234); arr14.add(0.0167); 
        arr15.add(0.0998); arr15.add(0.0495); arr15.add(0.0337); arr15.add(0.0260); arr15.add(0.0218); arr15.add(0.0165); arr15.add(0.0120); arr15.add(0.0083); 
        arr16.add(0.0000); arr16.add(0.0000); arr16.add(0.0000); arr16.add(0.0000); arr16.add(0.0000); arr16.add(0.0000); arr16.add(0.0000); arr16.add(0.0000); 
            
        return arr;
    }
    
    public void rStats(ArrayList<ArrayList> arr, ArrayList<Double> results, double factorNum, double runNum) {

    }
    
    public double findTransitionPeriodTime(PetriObjModel model) {
        double timeModeling = 1000;
        
        try{                
                
        ArrayList<Double> arr = new ArrayList<>();
        
        for(int i = 1; i < 10000; i++) {
            if(i%20 == 0){timeModeling += 20000;}
            PetriObjModel testModel = Kursach.getModel();
            testModel.setIsProtokol(false);
            timeModeling += 1000;
            testModel.go(timeModeling); 
                
            arr.add(responseVariable(testModel));

            if(arr.size() == 10) {

                double sum = 0;
                for(int j = 0; j < 10; j++) {
                    sum += arr.get(j);
                }   

                if((arr.get(0) / (sum/10)) >0.9 && (arr.get(0) / (sum/10)) < 1.1 &&
                   (arr.get(1) / (sum/10)) >0.9 && (arr.get(1) / (sum/10)) < 1.1 && 
                   (arr.get(2) / (sum/10)) >0.9 && (arr.get(2) / (sum/10)) < 1.1 &&
                   (arr.get(3) / (sum/10)) >0.9 && (arr.get(3) / (sum/10)) < 1.1 &&
                   (arr.get(4) / (sum/10)) >0.9 && (arr.get(4) / (sum/10)) < 1.1 &&
                   (arr.get(5) / (sum/10)) >0.9 && (arr.get(5) / (sum/10)) < 1.1 &&
                   (arr.get(6) / (sum/10)) >0.9 && (arr.get(6) / (sum/10)) < 1.1 &&
                   (arr.get(7) / (sum/10)) >0.9 && (arr.get(7) / (sum/10)) < 1.1 &&
                   (arr.get(8) / (sum/10)) >0.9 && (arr.get(8) / (sum/10)) < 1.1 &&
                   (arr.get(9) / (sum/10)) >0.9 && (arr.get(9) / (sum/10)) < 1.1) {
                    return timeModeling;
                }
                arr.remove(0);                
            }
        }        
        
        }
        catch(ExceptionInvalidNetStructure e) {
            System.out.println("setmodel error1");
        }
        catch(ExceptionInvalidTimeDelay e) {
            System.out.println("setmodel error2");
        }
        

        return timeModeling;
    }

    public int findRunNum(String distribution, double sigma, double epsilon, double beta) {        
        if(distribution == "norm") {
            return centralLimitTheorem(sigma, epsilon);
        }
        else {
            return chebishevInequality(sigma, epsilon, beta);
        }         
    }
    
    public int chebishevInequality(double sigma, double epsilon, double beta) {
        if(epsilon == 0 || sigma == 0 || beta == 1) {
        System.out.println("chebishevInequality error");
        }        
        return (int)Math.round((sigma*sigma)/(epsilon*epsilon*(1-beta)));
    }
    
    public int centralLimitTheorem(double sigma, double epsilon) {
        int a = (int)Math.round(sigma/epsilon);           
        switch(a) {
            case(1): return 4;
            case(2): return 15;
            case(4): return 61;
            case(6): return 138;
            case(8): return 246;
            case(10): return 384;
            default: return 384;
        } 
    }

    public double readFname(String fName) {         
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
    
    public void writeFbyName(String fName, double value, PetriObjModel model) {
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
    
    public double findXi(double x_value, double x_min, double x_max) {
        double x_0 = (x_max + x_min) / 2;
        double delta = (x_max - x_min) /2;
        return (x_value - x_0) / delta;
    } 
    
    public int[][] regressionAnalysis(int factorNum) {
        int rows = (int)Math.pow(2, factorNum);        
        int columns = rows; 
        int[][] a = new int[rows][columns];
        for(int i = 0; i < rows; i++) {
            a[i][0] = 1;
        }
        a = counterRecursion(rows, 1, a, factorNum, rows);
        
        if(factorNum == 2) {
            for(int i = 0; i < 4; i++) {
                a[i][3] = a[i][1] * a[i][2];
            }
        }
        if(factorNum == 3) {
            for(int i = 0; i < 8; i++) {
                a[i][4] = a[i][1] * a[i][2];
                a[i][5] = a[i][1] * a[i][3];
                a[i][6] = a[i][2] * a[i][3];
                a[i][7] = a[i][1] * a[i][2] * a[i][3];
            }
        }   
        if(factorNum == 4) {
            for(int i = 0; i < 16; i++) {        
                a[i][5] = a[i][1] * a[i][2]; //x1x2
                a[i][6] = a[i][1] * a[i][3]; 
                a[i][7] = a[i][1] * a[i][4];
                a[i][8] = a[i][2] * a[i][3];
                a[i][9] = a[i][2] * a[i][4];
                a[i][10] = a[i][3] * a[i][4];
                a[i][11] = a[i][1] * a[i][2] * a[i][3];
                a[i][12] = a[i][1] * a[i][2] * a[i][4];
                a[i][13] = a[i][1] * a[i][3] * a[i][4];
                a[i][14] = a[i][2] * a[i][3] * a[i][4];
                a[i][15] = a[i][1] * a[i][2] * a[i][3] * a[i][4];                
            }            
        }
        if(factorNum == 5) {
            for(int i = 0; i < 32; i++) {     
                a[i][6] = a[i][1] * a[i][2]; //x1x2
                a[i][7] = a[i][1] * a[i][3]; //x1x3
                a[i][8] = a[i][1] * a[i][4]; //x1x4
                a[i][9] = a[i][1] * a[i][5]; //x1x5
                a[i][10] = a[i][2] * a[i][3]; //x2x3
                a[i][11] = a[i][2] * a[i][4]; //x2x4
                a[i][12] = a[i][2] * a[i][5]; //x2x5
                a[i][13] = a[i][3] * a[i][4]; //x3x4
                a[i][14] = a[i][3] * a[i][5]; //x3x5
                a[i][15] = a[i][4] * a[i][5]; //x4x5
                a[i][16] = a[i][1] * a[i][2] * a[i][3]; //x1x2x3
                a[i][17] = a[i][1] * a[i][2] * a[i][4]; //x1x2x4
                a[i][18] = a[i][1] * a[i][2] * a[i][5]; //x1x2x5
                a[i][19] = a[i][1] * a[i][3] * a[i][4]; //x1x3x4
                a[i][20] = a[i][1] * a[i][3] * a[i][5]; //x1x3x5
                a[i][21] = a[i][1] * a[i][4] * a[i][5]; //x1x4x5
                a[i][22] = a[i][2] * a[i][3] * a[i][4]; //x2x3x4
                a[i][23] = a[i][2] * a[i][3] * a[i][5]; //x2x3x5
                a[i][24] = a[i][2] * a[i][4] * a[i][5]; //x2x4x5
                a[i][25] = a[i][3] * a[i][4] * a[i][5]; //x3x4x5
                a[i][26] = a[i][1] * a[i][2] * a[i][3] * a[i][4]; //x1x2x3x4               
                a[i][27] = a[i][1] * a[i][2] * a[i][3] * a[i][5]; //x1x2x3x5   
                a[i][28] = a[i][1] * a[i][2] * a[i][4] * a[i][5]; //x1x2x4x5   
                a[i][29] = a[i][1] * a[i][3] * a[i][4] * a[i][5]; //x1x3x4x5   
                a[i][30] = a[i][2] * a[i][3] * a[i][4] * a[i][5]; //x2x3x4x5   
                a[i][31] = a[i][1] * a[i][2] * a[i][3] * a[i][4] * a[i][5]; //x1x2x3x4x5   
            }               
        }
                 
        return a;
    }    
    
    public int[][] counterRecursion(int counter, int columnNum, int[][] arr, int factorNum, int rowNum) {
    int temp = counter;
    int temp2 = 0;
    for(int i = 0; i < rowNum; i++) {            
        if(counter + temp2 - i > counter/2) {
            arr[i][columnNum] = 1;
        }else{
            arr[i][columnNum] = -1;
        }      
        if(i+1 == temp) {temp2 += counter; temp += counter;}
    }
    if(columnNum == factorNum) {
        return arr;
    }
    else {
        return counterRecursion(counter/2, ++columnNum, arr, factorNum, rowNum);
    }
    }  
    
}


