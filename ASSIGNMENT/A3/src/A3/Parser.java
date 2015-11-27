package A3;

import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author ssm
 */
public class Parser {

  private static DefaultMutableTreeNode root;
  private static Vector<A4.Token> tokens;
  private static int currentToken;
  private static Gui gui;
  static int currentline;
  static int instruction_count;
  static int while_count;
  static int if_count;
  public static DefaultMutableTreeNode run(Vector<A4.Token> tokens2,Gui gui) {
    Parser.gui=gui;
    tokens = tokens2;
    currentToken = 0;
    root = new DefaultMutableTreeNode("program");
    //
    instruction_count=1;
    while_count=1;
    if_count=1;
    SemanticAnalyzer.Clear();
    CodeGenerator.clear(gui);
    rule_program(root);
    CodeGenerator.addInstruction("OPR","0","0");
    instruction_count++;
    //
    gui.writeSymbolTable(SemanticAnalyzer.getSymbolTable());
    
    CodeGenerator.writeCode(gui);
    return root;
    
  }
  private static boolean rule_program(DefaultMutableTreeNode parent) {
	  boolean error;
	    DefaultMutableTreeNode node;
    	currentline = tokens.get(currentToken).getLine();
	    if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("{")) {
	        node = new DefaultMutableTreeNode("{");
	        parent.add(node);
	        currentToken++;
	      }
	    else
	    {
	    	error=true;
	    	error(1,currentline);
	    	
	    }
	      node = new DefaultMutableTreeNode("body");
	      parent.add(node);
	      error = rule_body(node);
	      if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("}")) {
	      node = new DefaultMutableTreeNode("}");
	      parent.add(node);
	      currentToken++;
	    }
	      else
	      {			
	    	  error=true;
	    	  --currentToken;
	    	  error(2,currentline);
	    	  ++currentToken;
	    	  
	      }
	      return error;  
}  
  private static boolean rule_body(DefaultMutableTreeNode parent) {
	  boolean error=false;
	    DefaultMutableTreeNode node;
	    while (currentToken < tokens.size() && (!tokens.get(currentToken).getWord().equals("}"))) {
	    	currentline = tokens.get(currentToken).getLine();
	    	if(tokens.get(currentToken).getWord().equals("print")) {
	    		node = new DefaultMutableTreeNode("print");
	    		parent.add(node);
	    		error = rule_print(node);	
	    		if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(";")) {
	    			node = new DefaultMutableTreeNode(";");
	    			parent.add(node);
	    			currentToken++;	
	    		}
	    		else
	    		{
	    			error=true;
	    			error(3,currentline);
	    		}
	    	}
	    	else if( tokens.get(currentToken).getToken().equals("IDENTIFIER")) {
	    		node = new DefaultMutableTreeNode("assignment");
	    		parent.add(node);
	    		error = rule_assignment(node);	
	    		if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(";")) {
	    			node = new DefaultMutableTreeNode(";");
	    			parent.add(node);
	    			currentToken++;	
	    		}
	    		else{
	    			error=true;
	    			error(3,currentline);
	    		}
	  		 
	    	}
	    	else if (tokens.get(currentToken).getWord().equals("int") || tokens.get(currentToken).getWord().equals("float") || tokens.get(currentToken).getWord().equals("void") || tokens.get(currentToken).getWord().equals("boolean") || tokens.get(currentToken).getWord().equals("char") || tokens.get(currentToken).getWord().equals("string")) {
	    	node = new DefaultMutableTreeNode("variable");
	    	parent.add(node);
	    	error = rule_variable(node);

	    	 if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(";")) {
			    	node = new DefaultMutableTreeNode(";");
			        parent.add(node);
			        currentToken++;	
	    	 }
	    	 else{
	    		 error=true;
	    		 error(3,currentline);
	    	 	}	    		 	    	 
	    	}
	    	else if( tokens.get(currentToken).getWord().equals("while")) {
	    	node = new DefaultMutableTreeNode("while");
	  	    parent.add(node);
	  	    error = rule_while(node);	
	    	}
	    	else if( tokens.get(currentToken).getWord().equals("if")) {
	    	node = new DefaultMutableTreeNode("if");
	  	    parent.add(node);
	  	    error = rule_if(node);	
	    	}
	
	    	else if( tokens.get(currentToken).getWord().equals("return")) {
	    	node = new DefaultMutableTreeNode("return");
	  	    parent.add(node);
	  	    error = rule_return(node);	
	  	    if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(";")) {
		    	node = new DefaultMutableTreeNode(";");
		        parent.add(node);
		        currentToken++;	
		    }
	  	    else {
	  	    	error(3,currentline);
	  	    	error=true;
	  	
	    }
	    }
	    else
	  	{

	  	error = true; 
	  	error(4,currentline);
	  	while (currentToken < tokens.size() &&( !tokens.get(currentToken).getWord().equals(";") && !newline()))
	      {
	       ++currentToken;
	      }
	      if(currentToken < tokens.size() &&tokens.get(currentToken).getWord().equals(";"))
	      {
	       ++currentToken;
	      }
	    
	     }
	    }

	  //  error(4);
	    return error;
  }
  
  private static boolean rule_variable(DefaultMutableTreeNode parent)

  {

  boolean error = false;

  DefaultMutableTreeNode node;

  node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
  
  parent.add(node);
  String temp_type=tokens.get(currentToken).getWord();
  currentToken++;

  if(tokens.get(currentToken).getToken().equals("IDENTIFIER"))

  {		String temp_id=tokens.get(currentToken).getWord();
	  CodeGenerator.addVariable(temp_id, temp_type);
	  SemanticAnalyzer.checkVariable(tokens.get(currentToken-1).getWord(),tokens.get(currentToken).getWord(),currentline);
  node = new DefaultMutableTreeNode("identifier" + "(" + tokens.get(currentToken).getWord() + ")");

  parent.add(node);

  currentToken++;
  error=false;
  }

  else

  {

  error = true;
  error(6,currentline);
 // currentToken++; 

  }

  return error;

  }
  private static boolean rule_assignment(DefaultMutableTreeNode parent)	{
	  	boolean error = false;
	  	boolean expressionCheck=false;
	  	boolean nextline=false;
	    DefaultMutableTreeNode node;
	 //   if (currentToken < tokens.size() && tokens.get(currentToken).getToken().equals("IDENTIFIER")) {
	     
	      node = new DefaultMutableTreeNode("identifier" + "(" + tokens.get(currentToken).getWord() + ")");
	      String temp_id=tokens.get(currentToken).getWord();
	      parent.add(node);
	      SemanticAnalyzer.pushStack(tokens.get(currentToken).getWord(),currentline);
	      currentToken++;
	     //
	      if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("=")) {
	    	  node = new DefaultMutableTreeNode("=");
		      parent.add(node);
		      currentToken++;
		node=new DefaultMutableTreeNode("X");
		 parent.add(node);
		  error=rule_expression(node);
		  CodeGenerator.addInstruction("STO",temp_id,"0");
		  instruction_count++;
		  String x=SemanticAnalyzer.popStack();
	      String y=SemanticAnalyzer.popStack();
	      String result=SemanticAnalyzer.calculateCube(x, y, "=");
	      if(!result.equals("ok")){
	    	  SemanticAnalyzer.error(2,currentline);
	      }
	      }
	      else
	      {
	    	  error=true;
	    	  error(5,currentline);
	    	  expressionCheck=expressionSearch();
	    	  if(expressionCheck==false)
	    	  {
	    		   if (currentToken < tokens.size() && tokens.get(currentToken-1).getWord().equals(";"))
	    		   {
	    		    --currentToken;
	    		   }
	    		   if( tokens.get(currentToken).getLine() > currentline)
	    		   {
	    			   error(9,currentline-1);
	    			   //--currentToken;
	    		   }
	    		   else{
	    		   error(9,currentline);
	    		  }
	    	  }
	    		  else
	    		  {
	    		   node = new DefaultMutableTreeNode("X");
	    		         parent.add(node);
	    		   error=rule_expression(node);
	    		  }
	    		 }
	      
	    return error;
	  }
  
  
  
  private static boolean rule_while(DefaultMutableTreeNode parent) {
	  boolean error = false;
	  boolean testing=false;
	  boolean argcheck=false;
	  
	    DefaultMutableTreeNode node;
	      node = new DefaultMutableTreeNode("while");
	     String label1="#e"+while_count++;
	     String label2="#e"+while_count++;
	      parent.add(node);
	      currentToken++;
	    if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("(")) {
	      node = new DefaultMutableTreeNode("(");
	      parent.add(node);
	      currentToken++;
	    }
	    else 
	    {
	    	error=true;
	    	error(8,currentline);
	    }
	      //
	    CodeGenerator.addLabel(label1, instruction_count); //not needed for if 
	      node = new DefaultMutableTreeNode("expression");
	      parent.add(node);
	      error = rule_expression(node);
	      CodeGenerator.addInstruction("JMC",label2,"false"); 
	      instruction_count++;
	      String x=SemanticAnalyzer.popStack();
		    if(!x.equals("boolean")){
		    	SemanticAnalyzer.error(3,currentline);
		    }
	      //
	      if(error==true && currentToken< tokens.size() && !tokens.get(currentToken).getWord().equals(")")) 
	      {
	       argcheck=true;
	       ++currentToken;
	      }
	      else if(error==false && currentToken< tokens.size() && !tokens.get(currentToken).getWord().equals(")")) 
	      {
	       argcheck=true;
	      }
	      if(argcheck)
	      {

	       error(7,currentline);
	      }
	      
	      
	    if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(")")) { 
	    node = new DefaultMutableTreeNode(")");
	      parent.add(node);
	      currentToken++;      
	    } 
	    else {
	      error= true;
	      testing=search();
	      if(testing==false) {
	    	  error(7,currentline);
	      }
	      else
	      {
	    	  node = new DefaultMutableTreeNode(")");
	    	   parent.add(node);
	    	      currentToken++;
	      }
	    }
	    node = new DefaultMutableTreeNode("program");
	      parent.add(node);
	      rule_program(node);
	      CodeGenerator.addInstruction("JMP",label1,"0");
	      instruction_count++;
	      CodeGenerator.addLabel(label2,instruction_count);
	      return error;
  }

  private static boolean rule_if(DefaultMutableTreeNode parent) {
	  boolean error = false;
	  boolean testing=false;
	    DefaultMutableTreeNode node;
	    String label1="#e"+if_count++;
	     String label2="#e"+if_count++;
	     
	      node = new DefaultMutableTreeNode("if");
	      parent.add(node);
	      currentToken++;
	    if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("(")) {
	      node = new DefaultMutableTreeNode("(");
	      parent.add(node);
	      currentToken++;
	    }
	    else 
	    {
	    	error=true;
	    	error(8,currentline);
	    }
	      
	      node = new DefaultMutableTreeNode("expression");
	      parent.add(node);
	      error = rule_expression(node);
	      CodeGenerator.addInstruction("JMC",label1,"false");   // jump on condn to beginning of else 
	      instruction_count++;
	      String x=SemanticAnalyzer.popStack();
		    if(!x.equals("boolean")){
		    	SemanticAnalyzer.error(3,currentline);
		    }
	      if(error==true && currentToken< tokens.size() && !tokens.get(currentToken).getWord().equals(")")) 
	      {
	       ++currentToken;
	      }
	      
	    if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(")")) { 
	    node = new DefaultMutableTreeNode(")");
	      parent.add(node);
	      currentToken++;      
	    } 
	    else {
	      error= true;
	      testing=search();
	      if(testing=false){
	      error(7,currentline);
	    }
	      else
	      {
	       node = new DefaultMutableTreeNode(")");
	       parent.add(node);
	          currentToken++;
	      }
	      
	    }
	    node = new DefaultMutableTreeNode("program");
	      parent.add(node);
	      
	     error = rule_program(node);
	    
	     if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("else")) { 
	    	  CodeGenerator.addInstruction("JMP",label2,"0");   // jump unconditionally to else ending
		      instruction_count++;
	    	 CodeGenerator.addLabel(label1, instruction_count); // when if evaluates to false 
	    	  node = new DefaultMutableTreeNode("else");
	    	  parent.add(node);
		      currentToken++;
		      node = new DefaultMutableTreeNode("program");
	    	  parent.add(node);  
		     error=rule_program(node);
		     CodeGenerator.addLabel(label2, instruction_count);  // jump here after execution of if
	      }
	     else
	    	 CodeGenerator.addLabel(label1,instruction_count);  // jump here when there is no else 
	      
	    return error;
  }
 
  private static boolean rule_return(DefaultMutableTreeNode parent) {
	  boolean error=false;
	  DefaultMutableTreeNode node;
	      node = new DefaultMutableTreeNode("return");
	      CodeGenerator.addInstruction("OPR","1","0");
	      instruction_count++;
	      parent.add(node);
	      currentToken++;
	      return error;    
  }
	    
  private static boolean rule_print(DefaultMutableTreeNode parent) {
	  boolean error = false;
	  boolean testing=false;
	    DefaultMutableTreeNode node;
	      node = new DefaultMutableTreeNode("print");
	      parent.add(node);
	      currentToken++;
	    if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("(")) {
	    	
	      node = new DefaultMutableTreeNode("(");
	      parent.add(node);
	      currentToken++;
	    }
	    else 
	    {
	    	error=true;
	    	error(8,currentline);
	    }
	      //
	      node = new DefaultMutableTreeNode("expression");
	      parent.add(node);
	      error = rule_expression(node);
	      //
	      if(error==true && currentToken < tokens.size() && !tokens.get(currentToken).getWord().equals(")"))
	      {
	    	  ++currentToken;
	      }
	    if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(")")) { 
	    	CodeGenerator.addInstruction("OPR","21","0");
	    	instruction_count++;
	    	node = new DefaultMutableTreeNode(")");
	      parent.add(node);
	      currentToken++;      
	    } 
	    else {
	      error= true;
	      testing= search();
	     //   error(7,currentline);
	        if(testing==false){
	        error(7,currentline);
	        if ( tokens.get(currentToken-1).getWord().equals(";"))
	        {
	         --currentToken;
	        }
	        }
	        else
	        {
	        	   node = new DefaultMutableTreeNode(")");
	        	   parent.add(node);
	        	      currentToken++;
	        	  }
	        	  
	        	 
	        }
	    return error;
  }
 

  private static boolean rule_expression(DefaultMutableTreeNode parent) {
	  boolean error;
	    DefaultMutableTreeNode node = new DefaultMutableTreeNode("X");
	    parent.add(node);
	    error = rule_X(node);
	    while (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("|")) {
	    	node = new DefaultMutableTreeNode("|");
	        parent.add(node);
	        currentToken++;
	        node = new DefaultMutableTreeNode("X");
	        parent.add(node);
	        error = rule_X(node);
	        String x=SemanticAnalyzer.popStack();
	         String y=SemanticAnalyzer.popStack();
	         String result=SemanticAnalyzer.calculateCube(x,y,"|");
	         SemanticAnalyzer.pushStack(result,currentline);	     
	         CodeGenerator.addInstruction("OPR","8","0");
	         instruction_count++;
	    }
	    return error;  
	  
  }
  
  private static boolean rule_X(DefaultMutableTreeNode parent) {
	  boolean error;
	    DefaultMutableTreeNode node = new DefaultMutableTreeNode("Y");
	    parent.add(node);
	    error = rule_Y(node);
	    while (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("&")) {	    	
	        node = new DefaultMutableTreeNode("&");
	        parent.add(node);
	        currentToken++;
	        node = new DefaultMutableTreeNode("Y");
	        parent.add(node);
	        error = rule_Y(node);
	        String x=SemanticAnalyzer.popStack();
	         String y=SemanticAnalyzer.popStack();
	         String result=SemanticAnalyzer.calculateCube(x,y,"&");
	         SemanticAnalyzer.pushStack(result,currentline);
	         CodeGenerator.addInstruction("OPR","9","0");
	         instruction_count++;
	    }
	    return error;  
	  
  } 
  private static boolean rule_Y(DefaultMutableTreeNode parent) {
	  boolean error,isOpused=false;
	  DefaultMutableTreeNode node;
	    if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("!")) {
	    	isOpused=true;
	      node = new DefaultMutableTreeNode("!");
	      parent.add(node);
	      currentToken++;
	    }
	    node = new DefaultMutableTreeNode("R");
	    parent.add(node);
	    error = rule_R(node);
	    if(isOpused){
	    	String x=SemanticAnalyzer.popStack();
	    	String result=SemanticAnalyzer.calculateCube(x,"!");
	    	SemanticAnalyzer.pushStack(result,currentline);
	    	CodeGenerator.addInstruction("OPR","10","0");
	    	instruction_count++;
	    }
	    return error;
	  }
	  
    private static boolean rule_R(DefaultMutableTreeNode parent) {
    	boolean error;
        DefaultMutableTreeNode node;
        node = new DefaultMutableTreeNode("E");
        parent.add(node);
        error = rule_E(node);
        while (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(">") || currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("<") || currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("==") || currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("!=")) {
          if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(">")) {
        	node = new DefaultMutableTreeNode(">");
            parent.add(node);
            currentToken++;
            node = new DefaultMutableTreeNode("E");
            parent.add(node);
            error = rule_E(node);
            CodeGenerator.addInstruction("OPR","11","0");
      	  instruction_count++;
            String x=SemanticAnalyzer.popStack();
            String y=SemanticAnalyzer.popStack();
            String result=SemanticAnalyzer.calculateCube(x,y,">");
            SemanticAnalyzer.pushStack(result,currentline);
          } else if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("<")) {
        	 
            node = new DefaultMutableTreeNode("<");
            parent.add(node);
            currentToken++;
            node = new DefaultMutableTreeNode("E");
            parent.add(node);
            error = rule_E(node);
            CodeGenerator.addInstruction("OPR","12","0");
      	  instruction_count++;
            String x=SemanticAnalyzer.popStack();
            String y=SemanticAnalyzer.popStack();
            String result=SemanticAnalyzer.calculateCube(x,y,"<");
            SemanticAnalyzer.pushStack(result,currentline);
          } else if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("==")) {
        	  node = new DefaultMutableTreeNode("==");
              parent.add(node);
              currentToken++;
              node = new DefaultMutableTreeNode("E");
              parent.add(node);
              error = rule_E(node);
              CodeGenerator.addInstruction("OPR","15","0");
        	  instruction_count++;
             
              String x=SemanticAnalyzer.popStack();
              String y=SemanticAnalyzer.popStack();
              String result=SemanticAnalyzer.calculateCube(x,y,"==");
              SemanticAnalyzer.pushStack(result,currentline);
          } else if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("!=")) {
        	  node = new DefaultMutableTreeNode("!=");
              parent.add(node);
              currentToken++;
              node = new DefaultMutableTreeNode("E");
              parent.add(node);
              error = rule_E(node);
              CodeGenerator.addInstruction("OPR","16","0");
        	  instruction_count++;
        	 
              String x=SemanticAnalyzer.popStack();
              String y=SemanticAnalyzer.popStack();
              String result=SemanticAnalyzer.calculateCube(x,y,"!=");
              SemanticAnalyzer.pushStack(result,currentline);
             
            }  
        }
        return error;
      }

  private static boolean rule_E(DefaultMutableTreeNode parent) {
    boolean error;
    DefaultMutableTreeNode node;
    node = new DefaultMutableTreeNode("A");
    parent.add(node);
    error = rule_A(node);
    while (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("+") || currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("-")) {
      if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("+")) {
    	node = new DefaultMutableTreeNode("+");
        parent.add(node);
        currentToken++;
        node = new DefaultMutableTreeNode("A");
        parent.add(node);
        error = rule_A(node);
        CodeGenerator.addInstruction("OPR","2","0");
  	  instruction_count++;
      
        String x=SemanticAnalyzer.popStack();
        String y=SemanticAnalyzer.popStack();
        String result=SemanticAnalyzer.calculateCube(x,y,"+");
        SemanticAnalyzer.pushStack(result,currentline);
        
      } else if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("-")) {
    	 node = new DefaultMutableTreeNode("-");
        parent.add(node);
        currentToken++;
        node = new DefaultMutableTreeNode("A");
        parent.add(node);
        error = rule_A(node);
        CodeGenerator.addInstruction("OPR","3","0");
  	  instruction_count++;
      
        String x=SemanticAnalyzer.popStack();
        String y=SemanticAnalyzer.popStack();
        String result=SemanticAnalyzer.calculateCube(x,y,"-");
        SemanticAnalyzer.pushStack(result,currentline);
      }
    }
    return error;
  }

  private static boolean rule_A(DefaultMutableTreeNode parent) {
    boolean error,twice_here=false;
    DefaultMutableTreeNode node = new DefaultMutableTreeNode("B");
    parent.add(node);
    error = rule_B(node);
    while (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("*") || currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("/")) {
      if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("*")) {
    	node = new DefaultMutableTreeNode("*");
        parent.add(node);
        currentToken++;
        node = new DefaultMutableTreeNode("B");
        parent.add(node);
        error = rule_B(node);
        CodeGenerator.addInstruction("OPR","4","0");
  	  instruction_count++;
      
        String x=SemanticAnalyzer.popStack();
        String y=SemanticAnalyzer.popStack();
        String result=SemanticAnalyzer.calculateCube(x,y,"*");
   
        SemanticAnalyzer.pushStack(result,currentline);
        
      } else if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("/")) {
    	  	node = new DefaultMutableTreeNode("/");
        parent.add(node);
        node = new DefaultMutableTreeNode("B");
        parent.add(node);
        currentToken++;
        error = rule_B(node);
        CodeGenerator.addInstruction("OPR","5","0");
  	  	instruction_count++;
        String x=SemanticAnalyzer.popStack();
        String y=SemanticAnalyzer.popStack();
        String result=SemanticAnalyzer.calculateCube(x,y,"/");
        SemanticAnalyzer.pushStack(result,currentline);
      }
    }
    return error;
  }

  private static boolean rule_B(DefaultMutableTreeNode parent) {
    boolean error,isOpused=false;
    DefaultMutableTreeNode node;
    if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("-")) {
    	CodeGenerator.addInstruction("LIT","0","0");
    	instruction_count++;
      isOpused=true;
      node = new DefaultMutableTreeNode("-");
      parent.add(node);
      currentToken++;
    }
    
    node = new DefaultMutableTreeNode("C");
    parent.add(node);
    error = rule_C(node);
    if(isOpused){
    	CodeGenerator.addInstruction("OPR","3","0");
    	instruction_count++;
    	String x=SemanticAnalyzer.popStack();
    	String result=SemanticAnalyzer.calculateCube(x,"-");
    	SemanticAnalyzer.pushStack(result,currentline);
    }
    return error;
  }

  private static boolean rule_C(DefaultMutableTreeNode parent) {
    boolean error = false;
    String temp_type=tokens.get(currentToken).getWord();
    DefaultMutableTreeNode node;
    if (currentToken < tokens.size() && tokens.get(currentToken).getToken().equals("INTEGER")) {
    	CodeGenerator.addInstruction("LIT",temp_type,"0");
    	instruction_count++;
      SemanticAnalyzer.pushStack(tokens.get(currentToken).getToken(),currentline);
      node = new DefaultMutableTreeNode("integer" + "(" + tokens.get(currentToken).getWord() + ")");
      parent.add(node);
      currentToken++;
    } else if (currentToken < tokens.size() && tokens.get(currentToken).getToken().equals("IDENTIFIER")) {
    	CodeGenerator.addInstruction("LOD",temp_type,"0");
    	instruction_count++;
      SemanticAnalyzer.pushStack(tokens.get(currentToken).getWord(),currentline);
      node = new DefaultMutableTreeNode("identifier" + "(" + tokens.get(currentToken).getWord() + ")");
      parent.add(node);
      currentToken++;
    } else if (currentToken < tokens.size() && tokens.get(currentToken).getToken().equals("OCTAL")) {
    	CodeGenerator.addInstruction("LIT",temp_type,"0");
    	instruction_count++;
    	SemanticAnalyzer.pushStack(tokens.get(currentToken).getToken(),currentline);
    	node = new DefaultMutableTreeNode("octal" + "(" + tokens.get(currentToken).getWord() + ")");
        parent.add(node);
        currentToken++;
    } else if (currentToken < tokens.size() && tokens.get(currentToken).getToken().equals("HEXADECIMAL")) {
    	CodeGenerator.addInstruction("LIT",temp_type,"0");
    	instruction_count++;
    	SemanticAnalyzer.pushStack(tokens.get(currentToken).getToken(),currentline);
    	node = new DefaultMutableTreeNode("hexadecimal" + "(" + tokens.get(currentToken).getWord() + ")");
        parent.add(node);
        currentToken++;
    } else if (currentToken < tokens.size() && tokens.get(currentToken).getToken().equals("BINARY")) {
    	CodeGenerator.addInstruction("LIT",temp_type,"0");
    	instruction_count++;
    	SemanticAnalyzer.pushStack(tokens.get(currentToken).getToken(),currentline);
    	node = new DefaultMutableTreeNode("binary" + "(" + tokens.get(currentToken).getWord() + ")");
        parent.add(node);
        currentToken++;
    } else if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("true")) {
    	CodeGenerator.addInstruction("LIT",temp_type,"0");
    	instruction_count++;
    	SemanticAnalyzer.pushStack(tokens.get(currentToken).getToken(),currentline);
    	node = new DefaultMutableTreeNode("true" + "(" + tokens.get(currentToken).getToken() + ")");
        parent.add(node);
        currentToken++;
    } else if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("false")) {
    	CodeGenerator.addInstruction("LIT",temp_type,"0");
    	instruction_count++;
    	SemanticAnalyzer.pushStack(tokens.get(currentToken).getToken(),currentline);
    	node = new DefaultMutableTreeNode("false" + "(" + tokens.get(currentToken).getToken() + ")");
        parent.add(node);
        currentToken++;
    } else if (currentToken < tokens.size() && tokens.get(currentToken).getToken().equals("STRING")) {
    	CodeGenerator.addInstruction("LIT",temp_type,"0");
    	instruction_count++;
    	SemanticAnalyzer.pushStack(tokens.get(currentToken).getToken(),currentline);
    	node = new DefaultMutableTreeNode("string" + "(" + tokens.get(currentToken).getWord() + ")");
        parent.add(node);
        currentToken++;
    } else if (currentToken < tokens.size() && (tokens.get(currentToken).getToken().equals("CHAR") || tokens.get(currentToken).getToken().equals("CHARACTER"))) {
    	CodeGenerator.addInstruction("LIT",temp_type,"0");
    	instruction_count++;
    	SemanticAnalyzer.pushStack(tokens.get(currentToken).getToken(),currentline);
    	node = new DefaultMutableTreeNode("char" + "(" + tokens.get(currentToken).getWord() + ")");
        parent.add(node);
        currentToken++;
    } else if (currentToken < tokens.size() && tokens.get(currentToken).getToken().equals("FLOAT")) {
    	CodeGenerator.addInstruction("LIT",temp_type,"0");
    	instruction_count++;
    	SemanticAnalyzer.pushStack(tokens.get(currentToken).getToken(),currentline);
    	node = new DefaultMutableTreeNode("float" + "(" + tokens.get(currentToken).getWord() + ")");
        parent.add(node);
        currentToken++;
    } else if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("(")) {
      node = new DefaultMutableTreeNode("(");
      parent.add(node);
      currentToken++;
      //
      node = new DefaultMutableTreeNode("expression");
      parent.add(node);
      error = rule_expression(node);
      //
      if (currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(")")) {
      node = new DefaultMutableTreeNode(")");
      parent.add(node);
      currentToken++;      
    } else {
      //error = true;
      error(7,currentline);
    }
    }
    else {
    	error=true;
    	error(9,currentline);
    }
    return error;
  }
  private static boolean expressionSearch() {
	  boolean expressionCheck=false;
	  while(currentToken < tokens.size() && !(tokens.get(currentToken).getToken().equals("integer") ||tokens.get(currentToken).getToken().equals("octal") ||
	    tokens.get(currentToken).getToken().equals("hexadecimal") ||tokens.get(currentToken).getToken().equals("binary") ||
	    tokens.get(currentToken).getToken().equals("true") ||tokens.get(currentToken).getToken().equals("false") ||
	    tokens.get(currentToken).getToken().equals("string") ||tokens.get(currentToken).getToken().equals("char") ||
	    tokens.get(currentToken).getToken().equals("float") ||tokens.get(currentToken).getToken().equals("identifier") ||
	    tokens.get(currentToken).getToken().equals("(") ) && !newline() && tokens.get(currentToken).getLine() == currentline)
	  {
	   ++currentToken;
	  }
	  if(currentToken < tokens.size() && (tokens.get(currentToken).getToken().equals("integer") ||tokens.get(currentToken).getToken().equals("octal") ||
	    tokens.get(currentToken).getToken().equals("hexadecimal") ||tokens.get(currentToken).getToken().equals("binary") ||
	    tokens.get(currentToken).getToken().equals("true") ||tokens.get(currentToken).getToken().equals("false") ||
	    tokens.get(currentToken).getToken().equals("string") ||tokens.get(currentToken).getToken().equals("char") ||
	    tokens.get(currentToken).getToken().equals("float") ||tokens.get(currentToken).getToken().equals("identifier") ||
	    tokens.get(currentToken).getToken().equals("(") ) && ( tokens.get(currentToken).getLine() == currentline))
	  {
	   expressionCheck=true;
	  }
	  return expressionCheck;
	 }

	 private static boolean newline() {
	  if(currentline<tokens.get(currentToken).getLine())
	   return true;
	  else
	   return false;
	 }

	 private static boolean search() {
		 boolean testing=false;
		 while((currentToken < tokens.size() && !tokens.get(currentToken).getWord().equals(")") ) && !newline())
		 {
			// gui.writeConsole("token " + tokens.get(currentToken).getWord());
			// gui.writeConsole("token " + tokens.size() + " currenttoken " +currentToken);
		  ++currentToken;
		 }
		 if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(")") )
		 {
		  testing=true;
		 }
		 return testing;
		}



  public static void error(int err,int currentline) 
  {

    switch (err) 
    {
      case 1: gui.writeConsole("Line " + currentline + ": expected {"); 
              break;                 
      case 2: gui.writeConsole("Line " + currentline + ": expected }"); 
              break;                 
      case 3: gui.writeConsole("Line " + currentline + ": expected ;"); 
              break;                 
      case 4: gui.writeConsole("Line " +currentline+": expected identifier or keyword");
              break;                 
      case 5: gui.writeConsole("Line " +currentline+": expected =");
              break;                 
      case 6: gui.writeConsole("Line " +currentline+": expected identifier"); 
              break;                 
      case 7: gui.writeConsole("Line " +currentline+": expected )"); 
              break;                 
      case 8: gui.writeConsole("Line " +currentline+": expected ("); 
              break;                 
      case 9: gui.writeConsole("Line " +currentline+": expected value, identifier, (");
              break;
    }
  }
}
