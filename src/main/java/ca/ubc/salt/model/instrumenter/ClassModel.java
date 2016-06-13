package ca.ubc.salt.model.instrumenter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import ca.ubc.salt.model.utils.FileUtils;

public class ClassModel
{
    TypeDeclaration typeDec = null;
    CompilationUnit cu = null;
    List<FieldDeclaration> staticFields = null;
    List<Method> methods = null;

    public ClassModel(TypeDeclaration typeDec, CompilationUnit cu) throws IOException
    {
	this.typeDec = typeDec;
	this.cu = cu;
    }
    
    
    public static List<ClassModel> getClasses(String source) throws IOException
    {
	return getClasses(source, false, null, null, null);
    }
    public static List<ClassModel> getClasses(String source, boolean binding, String unitName, String[] sources,
	    String[] classPath) throws IOException
    {
	ASTParser parser = ASTParser.newParser(AST.JLS8);
	if (binding)
	    parser.setResolveBindings(true);
	parser.setKind(ASTParser.K_COMPILATION_UNIT);

	if (binding)
	    parser.setBindingsRecovery(true);
	Map pOptions = JavaCore.getOptions();
	pOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
	pOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
	pOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
	parser.setCompilerOptions(pOptions);

	if (binding)
	{
	    // String unitName = "FractionTest.java";
	    parser.setUnitName(unitName);

	    // String[] sources = { "C:\\Users\\pc\\workspace\\asttester\\src"
	    // };
	    // String[] classpath = {"C:\\Program
	    // Files\\Java\\jre1.8.0_25\\lib\\rt.jar"};
	    parser.setEnvironment(classPath, sources, new String[] { "UTF-8"}, true);
	}
	parser.setSource(source.toCharArray());
	CompilationUnit cu = (CompilationUnit) parser.createAST(null);

	List typeDeclarationList = cu.types();

	List<ClassModel> classes = new ArrayList<ClassModel>();

	for (Object type : typeDeclarationList)
	{
	    // TODO What about other types
	    if (type instanceof TypeDeclaration)
	    {
		classes.add(new ClassModel((TypeDeclaration) type, cu));
	    }
	}

	return classes;
    }

    private void initMethods()
    {

	methods = new ArrayList<Method>();
	MethodDeclaration[] methodList = typeDec.getMethods();

	for (MethodDeclaration m : methodList)
	{
	    // System.out.println(method.getName());
	    // for (VariableDeclarationFragment var : visitor.varDecs)
	    // System.out.println(" " + var.getName());

	    Method method = new Method(m, typeDec.getName().toString());
	    methods.add(method);
	}
    }

    private void initStaticFields()
    {
	FieldDeclaration[] fields = typeDec.getFields();
	staticFields = new ArrayList<FieldDeclaration>();
	for (FieldDeclaration field : fields)
	{
	    if (Modifier.isStatic(field.getModifiers()))
	    {
		staticFields.add(field);
	    }
	}
    }

    public TypeDeclaration getTypeDec()
    {
	return typeDec;
    }

    public void setTypeDec(TypeDeclaration typeDec)
    {
	this.typeDec = typeDec;
    }

    public CompilationUnit getCu()
    {
	return cu;
    }

    public void setCu(CompilationUnit cu)
    {
	this.cu = cu;
    }

    public List<FieldDeclaration> getStaticFields()
    {
	return staticFields;
    }

    public void setStaticFields(List<FieldDeclaration> staticFields)
    {
	if (staticFields == null)
	    initStaticFields();
	this.staticFields = staticFields;
    }

    public List<Method> getMethods()
    {
	if (methods == null)
	    initMethods();
	return methods;
    }

    public void setMethods(List<Method> methods)
    {
	this.methods = methods;
    }

}
