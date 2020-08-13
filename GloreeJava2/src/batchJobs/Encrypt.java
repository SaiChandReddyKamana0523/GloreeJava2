package batchJobs;




public class Encrypt {

	public static void main(String[] args) {
		
		
		String input = args[0];
		//String input = "";
		String debug = "";
		
		try {
			debug = args[1];
		}
		catch (Exception e){
			
		}
		
		
		if (debug == null ){
			debug = "";
		}
		
		String output = "";
		
		
		
		try {
			char arr[] = input.toCharArray(); // convert the String object to array of char

			
			// iterate over the array using the for-each loop.       
			for(int i=0; i<arr.length; i++){
			    char c = arr[i];
			    int charInt = (int) c;
			    charInt  = charInt + 25;
			    charInt = charInt * 129;
			    output += charInt + "-";
			    
			}

			if (output.contains("-")){
				output = (String) output.subSequence(0,output.lastIndexOf("-"));
			}	
				
			System.out.println("Encrypted value is " + output);
			
			String reconfiguredInput = "";
			
			// lets figure out the input.
			String[] enA = output.split("-");
			for (int j=0; j< enA.length; j++ ) {
				int charInt = Integer.parseInt(enA[j]);
				charInt = charInt / 129;
				charInt = charInt - 25;
				char c = (char) charInt;
				reconfiguredInput += c;
				
			}
			if (debug.equals("debug")){
				System.out.println("reconfigured output is " + reconfiguredInput);
			}
		}
		catch (Exception e){

			e.printStackTrace();
		}
	
		
	}
}
