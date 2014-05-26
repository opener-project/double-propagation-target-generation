package org.openerproject.double_propagation2.main;

import static org.openerproject.double_propagation2.main.GlobalVariables.*;

public class Main2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void execute(String[]args){
		try{
		if(args.length==0){
			throw new RuntimeException("Wrong parameters");
		}
		//Read arg[0] as the switching parameter
		String operation=args[0];
		if(operation.equals(MULTIWORDS_OPT)){
			
			
		}else if(operation.equals(DOUBLE_PROPAGATION_OPT)){
			
		}
		
		}catch(Exception e){
			
		}
	}
	
}
