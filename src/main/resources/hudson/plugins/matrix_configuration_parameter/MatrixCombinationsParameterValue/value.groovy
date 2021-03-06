package hudson.plugins.matrix_configuration_parameter.matrixcombinationparametervalue

import hudson.matrix.AxisList
import hudson.matrix.Combination
import hudson.matrix.Layouter
import hudson.matrix.MatrixBuild
import hudson.matrix.MatrixProject
import hudson.plugins.matrix_configuration_parameter.MatrixCombinationsParameterValue
import lib.LayoutTagLib
import org.kohsuke.stapler.jelly.groovy.Namespace

l = namespace(LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")
f = namespace("lib/form")
nsProject = namespace("/hudson/plugins/matrix_configuration_parameter/taglib")

MatrixCombinationsParameterValue valueIt = it;
MatrixProject project = request.findAncestorObject(MatrixProject.class);
MatrixBuild build = request.findAncestorObject(MatrixBuild.class);
if (project == null || build == null) {
    //in case you are looking at a specific run, MatrixRun Ancestor will replace the MatrixBuild
    f.entry(title: valueIt.getName(), description: it.getDescription()) {
        div(name: "parameter") {
            input(type: "hidden", name: "name", value: valueIt.getName())
            text(_("Not applicable. Applicable only to multi-configuration projects."))
        }//div
    }
    return;
}
AxisList axes = project.getAxes();
Layouter layouter = new Layouter<Combination>(axes) {
    protected Combination getT(Combination c) {
        return c;
    }
};

drawParameterBody(f, valueIt, axes, project, build, layouter);



private void drawParameterBody(Namespace f,MatrixCombinationsParameterValue valueIt,AxisList axes,MatrixProject project,MatrixBuild build,Layouter layouter) {
    f.entry(title: valueIt.getName(), description: it.getDescription()) {
        div(name: "parameter") {
            input(type: "hidden", name: "name", value: valueIt.getName())
            nsProject.matrix(it: build, layouter: layouter) {
              drawTableBall(p, project.axes, valueIt, project, build, layouter);
            }
        }//div
    }
}

private void drawTableBall(Combination combination,AxisList axes,MatrixCombinationsParameterValue matrixValue,MatrixProject project,MatrixBuild build,Layouter layouter) {

    run = build.getRun(combination);
    result = matrixValue.combinationExists(axes, combination);
    if (run != null && result){
        a(href:rootURL+"/"+run.getUrl()){
            img(src: "${imagesURL}/24x24/"+run.getBuildStatusUrl());
            if (!layouter.x || !layouter.y) {
                text(combination.toString(layouter.z))
              }
            f.checkbox(checked: "true",onclick:"return false;", onkeydown:"return false;", name: "values",id: String.format("checkbox%s-%s", matrixValue.getName(), combination.toString('-' as char, '-' as char)));
            input(type: "hidden", name: "confs", value: combination.toString());
        }

    } else {
        img(src: "${imagesURL}/24x24/grey.gif");
        if (!layouter.x || !layouter.y) {
            text(combination.toString(layouter.z))
          }
        f.checkbox(checked: "false",onclick:"return false;", onkeydown:"return false;", name: "values",id: String.format("checkbox%s-%s", matrixValue.getName(), combination.toString('-' as char, '-' as char)));
        input(type: "hidden", name: "confs", value: combination.toString());
    }
}
