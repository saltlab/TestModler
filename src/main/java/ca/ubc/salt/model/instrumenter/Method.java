package ca.ubc.salt.model.instrumenter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import ca.ubc.salt.model.state.ReadVariableVisitor;

public class Method
{
    MethodDeclaration methodDec = null;

    public Method(MethodDeclaration methodDec)
    {
	this.methodDec = methodDec;
    }

    public void instrumentMethod(ASTRewrite rewriter, Document document, List<String> loadedClassVars)
	    throws JavaModelException, IllegalArgumentException, MalformedTreeException, BadLocationException
    {
	int randomNumber = (int) (Math.random() * (Integer.MAX_VALUE - 1));
	Block block = methodDec.getBody();
	ListRewrite listRewrite = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY);

	Block header = (Block) TestClassInstrumenter.generateInstrumentationHeader(randomNumber,
		methodDec.getName().toString());
	List<Statement> stmts = header.statements();
	for (int i = stmts.size() - 1; i >= 0; i--)
	    listRewrite.insertFirst(stmts.get(i), null);

	// Block footer =
	// (Block)TestClassInstrumenter.generateFooterBlock(randomNumber);
	// stmts = footer.statements();
	// for (int i = stmts.size() - 1; i >= 0; i--)
	// listRewrite.insertLast(stmts.get(i), null);
	// listRewrite.insertLast(footer, null);

	InstrumenterVisitor visitor = new InstrumenterVisitor(rewriter, randomNumber, methodDec.getName().toString());
	this.methodDec.accept(visitor);

	// apply the text edits to the compilation unit

	// edits.apply(document);
	//
	// // this is the code for adding statements
	// System.out.println(document.get());

    }

    public void populateReadVars(Document document, List<String> loadedClassVars, Map<String, Set<SimpleName>> readVars)
    {
	ReadVariableVisitor visitor = new ReadVariableVisitor(methodDec.getName().toString());
	visitor.setReadVars(readVars);
	this.methodDec.accept(visitor);
//	System.out.println(visitor.getReadVars());
    }

}
