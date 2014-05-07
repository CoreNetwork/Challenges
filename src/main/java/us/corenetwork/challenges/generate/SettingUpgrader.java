package us.corenetwork.challenges.generate;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingUpgrader {
    private static int fieldsChanged = 0;

    public static void main(String args[]) throws IOException, ParseException {
        File yml = new File(args[0]);
        File java = new File(args[1]);
        Yaml yaml = new Yaml();
        Object data = yaml.load(new FileReader(yml));

        CompilationUnit cu = JavaParser.parse(java);

        parseData(data, "", cu);

        if (fieldsChanged == 0) {
            System.out.println("File is already up to date. No changes necessary.");
            return;
        }

        System.out.println("\n==================\nNew code:\n==================\n");

        System.out.println(cu.toString());
    }

    private static void parseData(Object data, String path, CompilationUnit cu) {
        if (data instanceof HashMap) {
            HashMap<String, Object> map = (HashMap<String, Object>) data;
            for (Map.Entry<String, Object> e : map.entrySet()) {
                String key = e.getKey();
                parseData(e.getValue(), path+(path.isEmpty()?"":".")+key, cu);
            }
        } else {

            new EnumEntryVisitor(path, data).visit(cu, null);
        }
    }

    private static class EnumEntryVisitor extends VoidVisitorAdapter {
        private String path;
        private Object data;

        private EnumEntryVisitor(String path, Object data) {
            this.path = path;
            this.data = data;
        }

        @Override
        public void visit(EnumConstantDeclaration e, Object arg) {
            Expression expr = e.getArgs().get(0);
            if (expr instanceof StringLiteralExpr) {
                StringLiteralExpr sle = (StringLiteralExpr) expr;
                if (sle.getValue().equals(path)) {
                    Expression value = null;
                    if (data instanceof String) {
                        value = new StringLiteralExpr((String) data);
                    } else if (data.getClass() == Integer.TYPE) {
                        value = new IntegerLiteralExpr(data.toString());
                    }
                    if (value != null) {
                        if (!e.getArgs().get(1).equals(value)) {
                            e.getArgs().set(1, value);
                            System.out.println("Setting " + path + " to '" + data + "'");
                            fieldsChanged++;
                        }
                    }
                }
            }
        }
    }
}
