package A3;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class SemanticAnalyzer {
  
  private static final Hashtable<String, Vector<SymbolTableItem>> symbolTable = new Hashtable<String, Vector<SymbolTableItem>>();
  private static final Stack<String> stack = new Stack();
  private static Gui gui;
  
  // create here a data structure for the cube of types
  private static String[][] plus= new String[][]{
	 //int		float		char	 string 	bool	void	  error
	  {"int"   , "float" , "error", "string", "error", "error", "error"}, //int
	  {"float" , "float" , "error", "string", "error", "error", "error"}, //float
	  {"error" , "error" , "error", "string", "error", "error", "error"}, //char
	  {"string", "string", "string","string", "string","error", "error"}, //string
	  {"error" , "error" , "error", "string", "error", "error", "error"}, //boolean
	  {"error" , "error" , "error", "error", "error", "error", "error"}, //void 
	  {"error" , "error" , "error", "error", "error", "error", "error"}, //error
  };

  private static String[][] binary_minus_multiply_divide= new String[][]{
	//int		float		char	 string 	bool	void	  error
	  {"int"   , "float" , "error", "error", "error", "error", "error"}, //int
	  {"float" , "float" , "error", "error", "error", "error", "error"}, //float
	  {"error" , "error" , "error", "error", "error", "error", "error"}, //char
	  {"error" , "error" , "error", "error", "error", "error", "error"},// string
	  {"error" , "error" , "error", "error", "error", "error", "error"},//bool
	  {"error" , "error" , "error", "error", "error", "error", "error"},//void
	  {"error" , "error" , "error", "error", "error", "error", "error"}, //err
  };
  
  
  private static String[][] less_greater_than= new String[][]{
	//int		 float		 char	   string 	bool	 void	  error
	  {"boolean", "boolean" , "error", "error", "error", "error", "error"}, //int
	  {"boolean", "boolean" , "error", "error", "error", "error", "error"}, //float
	  {"error" 	, "error" 	, "error", "error", "error", "error", "error"}, //char
	  {"error" 	, "error" 	, "error", "error", "error", "error", "error"}, //string
	  {"error" 	, "error" 	, "error", "error", "error", "error", "error"},//bool
	  {"error" 	, "error" 	, "error", "error", "error", "error", "error"},//void
	  {"error" 	, "error" 	, "error", "error", "error", "error", "error"},//err
  };
    
  private static String[][] not_double_equals= new String[][]{
	  //int			float		char	 string 		bool		void	  error
	  {"boolean", "boolean" , "error"	, "error"	, "error"	, "error", "error"}, //int
	  {"boolean", "boolean" , "error"	, "error"	, "error"	, "error", "error"}, //float
	  {"error" 	, "error" 	, "boolean"	, "error"	, "error"	, "error", "error"}, //char
	  {"error" 	, "error" 	, "error"	, "boolean"	, "error"	, "error", "error"},//string
	  {"error" 	, "error" 	, "error"	, "error"	, "boolean"	, "error", "error"}, //bool
	  {"error" 	, "error" 	, "error"	, "error"	, "error"	, "error", "error"}, //void
	  {"error" 	, "error" 	, "error"	, "error"	, "error"	, "error", "error"}, //err
  };  

  
  private static String[][] logical_and_or= new String[][]{
	//int		float		char	 string 	bool		void	  error
	  {"error" , "error" , "error", "error", "error"	, "error", "error"},//int
	  {"error" , "error" , "error", "error", "error"	, "error", "error"}, //float
	  {"error" , "error" , "error", "error", "error"	, "error", "error"}, //char
	  {"error" , "error" , "error", "error", "error"	, "error", "error"},//string
	  {"error" , "error" , "error", "error", "boolean"	, "error", "error"},//bool
	  {"error" , "error" , "error", "error", "error"	, "error", "error"},//void
	  {"error" , "error" , "error", "error", "error"	, "error", "error"},//err
  };
  
  private static String[][] unary_not= new String[][]{
	//int		float char   string  bool         void  error
	{"error","error","error","error","boolean","error","error"}
  };

  private static String[][] unary_minus=new String[][]{
	// int		float char   string  bool         void  error
	{"int","float","error","error","error","error","error"}
  };

  private static String[][] equals= new String[][]{
	//int		float		char	 string 	bool		void	  error
	  {"ok"    , "error" , "error", "error", "error"	, "error", "error"},//int
	  {"ok"    , "ok" 	 , "error", "error", "error"	, "error", "error"},//float
	  {"error" , "error" , "ok"	  , "error", "error"	, "error", "error"},//char
	  {"error" , "error" , "error", "ok"   , "error"	, "error", "error"},//string
	  {"error" , "error" , "error", "error", "ok"	    , "error", "error"},//bool
	  {"error" , "error" , "error", "error", "error"	, "ok"   , "error"},//void
	  {"error" , "error" , "error", "error", "error"	, "error", "error"},//err
  };
  
  public static Hashtable<String, Vector<SymbolTableItem>> getSymbolTable() {
    return symbolTable;
  }
  
  public static void initGui(Gui gui){
	  SemanticAnalyzer.gui=gui;
  }
  
  public static void Clear(){
	  symbolTable.clear();
  }
  
  public static void checkVariable(String type, String id,int currentline) {
   
    // A. search the id in the symbol table
	//  boolean isAvailable=symbolTable.containsKey(id);
	   
    // B. if !exist then insert: type, scope=global, value={0, false, "", '')

	if(!symbolTable.containsKey(id))
	{
	Vector v = new Vector();
    v.add(new SymbolTableItem(type,"global", ""));
    symbolTable.put(id, v);
	}
    // C. else error: “variable id is already defined”
	else
	{
		error(1,currentline);
	}
  }

  public static void pushStack(String type,int currentline) {
	 String x = "";
	  // push type in the stack
	  if(type.equals("INTEGER")||type.equals("int")||type.equals("OCTAL")||type.equals("HEXADECIMAL")||type.equals("BINARY")){
		 x="int";  
	  }
  
	  else if(type.equalsIgnoreCase("STRING")){
		x="string";  
	  }
	  
	  else if(type.equalsIgnoreCase("char")||type.equalsIgnoreCase("CHARACTER")){
		  x="char";  
	  }
	  
	  else if(type.equalsIgnoreCase("FLOAT")){
		  x="float";  
	  }
	  
	  else if(type.equals("KEYWORD")||type.equals("boolean")){
		  x="boolean";  
	  }
	  else if(type.equals("error")){
		  x="error";
	  }
	  else {
	//	  gui.writeConsole("var "+type);
		  if(symbolTable.containsKey(type)){
			  
			  x += symbolTable.get(type).get(0).getType();
			  
		  }
		  else { //variable id not found
			  error(0,currentline);
			  x = "error";
		  }
	  }
 // gui.writeConsole("type "+x);
	  stack.push(x);
  } 
  
  public static String popStack() {
    String result="";
    // pop a value from the stack
    if(!stack.empty()){
    result=stack.pop();
    }
    return result;
  }
  
  
  public static String calculateCube(String type, String operator) {
    String result="";
    int col=6;
    if(type.equals("int")){
    	col=0;
    }
    else if(type.equals("float")){
    	col=1;
    }
    else if(type.equals("char")){
    	col=2;
    }
    else if(type.equals("string")){
    	col=3;
    }
    else if(type.equals("boolean")){
    	col=4;
    }
    else if(type.equals("void")){
    	col=5;
    }
    else if(type.equals("error")){
    	col=6;
    }
    if(operator.equals("-")){
    	result=unary_minus[0][col];
    }
    else if(operator.equals("!")){
    	result=unary_not[0][col];
    }
    // unary operator ( - and !)
    
    return result;
  }

  public static String calculateCube(String type1, String type2, String operator) {
    String result="";
    int row=6,col=6;
    // binary operator ( - and !)
    if(type1.equals("int")){
    	row=0;
    }
    else if(type2.equals("float")){
    	row=1;
    }
    else if(type2.equals("char")){
    	row=2;
    }
    else if(type2.equals("string")){
    	row=3;
    }
    else if(type2.equals("boolean")){
    	row=4;
    }
    else if(type2.equals("void")){
    	row=5;
    }
    else if(type2.equals("error")){
    	row=6;
    }
    if(type2.equals("int")){
    	col=0;
    }
    else if(type1.equals("float")){
    	col=1;
    }
    else if(type1.equals("char")){
    	col=2;
    }
    else if(type1.equals("string")){
    	col=3;
    }
    else if(type1.equals("boolean")){
    	col=4;
    }
    else if(type1.equals("void")){
    	col=5;
    }
    else if(type1.equals("error")){
    	col=6;
    }
    if(operator.equals("+")){
    	result= plus[row][col];
    }
    else if(operator.equals("-")||operator.equals("*")||operator.equals("/")){
    	result=binary_minus_multiply_divide[row][col];
    }
    else if(operator.equals("<")||(operator.equals(">"))){
    	result=less_greater_than[row][col];
    //	gui.writeConsole("result "+result);
    }
    else if(operator.equals("&")||(operator.equals("|"))){
    	result=logical_and_or[row][col];
    }
    else if(operator.equals("!=")||(operator.equals("=="))){
    	result=not_double_equals[row][col];
    }
    else if(operator.equals("=")){
    	result=equals[row][col];
    }
//    gui.writeConsole("Row "+row+" column " +col);
//    gui.writeConsole("Result " +result);
    return result;
  }
  
  public static void error(int err, int n) {
    switch (err) {
      case 0:
    	gui.writeConsole("Line" + n + ": variable id not found");
    	break;
      case 1: 
        gui.writeConsole("Line" + n + ": variable id is already defined"); 
        break;
      case 2: 
        gui.writeConsole("Line" + n + ": incompatible types: type mismatch"); 
        break;
      case 3: 
        gui.writeConsole("Line" + n + ": incompatible types: expected boolean"); 
        break;

    }
  }
  
}
