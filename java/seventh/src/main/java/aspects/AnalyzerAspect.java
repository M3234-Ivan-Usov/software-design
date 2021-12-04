package aspects;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
public class AnalyzerAspect {
    public static final String ROOT_PACKAGE = "ru.ifmo.rain";

    private static class AnalyzerNode {
        public final String name;
        public int calls = 0;
        public long avgTime = 0;
        public final boolean isMethod;
        public final Map<String, AnalyzerNode> subTree;

        public static AnalyzerNode makeSubTree(String name) {
            return new AnalyzerNode(name, false, new HashMap<>());
        }

        public static AnalyzerNode makeLeaf(String name) {
            return new AnalyzerNode(name, true, Collections.emptyMap());
        }

        private AnalyzerNode(String name, boolean isMethod, Map<String, AnalyzerNode> subTree) {
            this.name = name;
            this.isMethod = isMethod;
            this.subTree = subTree;
        }

        public void update(long measured) {
            avgTime = (avgTime * calls + measured / 1000) / ++calls;
        }

        private String timeMeasure() {
            if (avgTime < 1000L) return avgTime + " ns";
            else if (avgTime < 1000000L) return avgTime / 1000L + " mu";
            else if (avgTime < 1000000000L) return avgTime / 1000000L + " ms";
            else return avgTime / 1000000000L + " s";
        }

        @Override
        public String toString() {
            return name + " [" + calls + ", " + timeMeasure() + "]";
        }
    }

    private static final AnalyzerNode analyzerRoot = AnalyzerNode.makeSubTree(ROOT_PACKAGE);
    private static final String pointcutLocation = "execution(* " + ROOT_PACKAGE + "..*(..))";
    private static final Pattern methodRegex = Pattern.compile("[0-9a-zA-z]+\\(.*\\)");
    private static final int prefixLength = ROOT_PACKAGE.length() + 1;


    private void update(String[] fullName, String shortSignature, long measured) {
        analyzerRoot.update(measured);
        AnalyzerNode node = analyzerRoot;
        for (String subName: fullName) {
            node = node.subTree.computeIfAbsent(subName, AnalyzerNode::makeSubTree);
            node.update(measured);
        }
        node = node.subTree.computeIfAbsent(shortSignature, AnalyzerNode::makeLeaf);
        node.update(measured);
    }

    @Pointcut(pointcutLocation)
    public void analyzerPointcut() {}

    @Around(value = "analyzerPointcut()")
    public Object analyzerMain(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature key = joinPoint.getSignature();
        String[] fullName =  key.getDeclaringTypeName().substring(prefixLength).split("\\.");
        String fullSign = key.toString();
        // System.out.println(fullSign);
        String retType = fullSign.split(" ")[0];
        Matcher matcher = methodRegex.matcher(fullSign);
        boolean findResult = matcher.find();
        String methodShortName = fullSign.substring(matcher.start(), matcher.end());
        long start = System.nanoTime();
        Object result = joinPoint.proceed();
        long measured = System.nanoTime() - start;
        update(fullName, retType + " " + methodShortName, measured);
        return result;
    }

    public static void show() {
        DelegateTree<AnalyzerNode, Integer> tree =
                new DelegateTree<>(new DirectedOrderedSparseMultigraph<>());
        tree.setRoot(analyzerRoot);
        Stack<AnalyzerNode> treeDfs = new Stack<>();
        int edgeId = 0;
        treeDfs.push(analyzerRoot);
        while (!treeDfs.empty()) {
            AnalyzerNode subRoot = treeDfs.pop();
            for (AnalyzerNode node : subRoot.subTree.values()) {
                tree.addChild(edgeId++, subRoot, node);
                if (!node.isMethod) treeDfs.push(node);
            }
        }
        Layout<AnalyzerNode, Integer> layout = new TreeLayout<>(tree, 180, 80);
        BasicVisualizationServer<AnalyzerNode, Integer> view = new BasicVisualizationServer<>(layout);
        view.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.N);
        view.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        view.getRenderContext().setVertexFillPaintTransformer(x -> (x.isMethod)? Color.GREEN : Color.RED);
        JFrame frame = new JFrame("Analyzed package: " + ROOT_PACKAGE);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
