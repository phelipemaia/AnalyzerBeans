package org.eobjects.analyzer.beans.valuedist
import org.eobjects.analyzer.configuration.AnalyzerBeansConfigurationImpl
import org.eobjects.analyzer.data.MockInputColumn
import org.eobjects.analyzer.data.MockInputRow
import org.eobjects.analyzer.descriptors.ClasspathScanDescriptorProvider
import org.eobjects.analyzer.result.html.DefaultHtmlRenderingContext
import org.eobjects.analyzer.result.renderer.RendererFactory
import org.junit.Assert
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

class ValueDistributionResultHtmlRendererTest extends AssertionsForJUnit {

  @Test
  def testNoUniqueValuesStored = {
    val context = new DefaultHtmlRenderingContext();

    val col1 = new MockInputColumn[String]("email", classOf[String]);

    val analyzer = new ValueDistributionAnalyzer(col1, false, null, null);
    analyzer.run(new MockInputRow().put(col1, "kasper@eobjects.dk"), 1);
    analyzer.run(new MockInputRow().put(col1, "kasper.sorensen@humaninference.com"), 1);
    analyzer.run(new MockInputRow().put(col1, "foo@bar"), 2);

    val result = analyzer.getResult()

    Assert.assertEquals(2, result.getUniqueCount());

    val htmlFragment = new ValueDistributionResultHtmlRenderer(createRendererFactory()).render(result);
    htmlFragment.initialize(context);

    val bodyElems = htmlFragment.getBodyElements()
    Assert.assertEquals(3, bodyElems.size());

    Assert.assertEquals("""<div class="valueDistributionResultContainer">
                 <div class="valueDistributionGroupPanel">
             
             <div class="valueDistributionChart" style="height: 50px;" id="reselem_1">
               </div>
             <table class="valueDistributionValueTable">
                   <tr><td>&lt;unique&gt;</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_2'); return false;" href="#">2</a></td></tr><tr><td>foo@bar</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_3'); return false;" href="#">2</a></td></tr>
                 </table>
             <table class="valueDistributionSummaryTable">
               <tr><td>Total count</td><td>4</td></tr>
               <tr><td>Distinct count</td><td>3</td></tr>
             </table>
           </div>
               </div>""".replaceAll("\r\n", "\n"), bodyElems.get(2).toHtml(context).replaceAll("\r\n", "\n"));
  }

  @Test
  def testRenderMultipleGroups = {
    val context = new DefaultHtmlRenderingContext();

    val col1 = new MockInputColumn[String]("email username", classOf[String]);
    val col2 = new MockInputColumn[String]("email domain", classOf[String]);

    val analyzer = new ValueDistributionAnalyzer(col1, col2, true, null, null);

    analyzer.run(new MockInputRow().put(col1, "kasper").put(col2, "eobjects.dk"), 4);
    analyzer.run(new MockInputRow().put(col1, "kasper.sorensen").put(col2, "eobjects.dk"), 2);
    analyzer.run(new MockInputRow().put(col1, "info").put(col2, "eobjects.dk"), 1);
    analyzer.run(new MockInputRow().put(col1, null).put(col2, "eobjects.dk"), 1);
    analyzer.run(new MockInputRow().put(col1, "kasper.sorensen").put(col2, "humaninference.com"), 1);
    analyzer.run(new MockInputRow().put(col1, "winfried.vanholland").put(col2, "humaninference.com"), 1);
    analyzer.run(new MockInputRow().put(col1, "kaspers").put(col2, "humaninference.com"), 1);

    val result = analyzer.getResult();

    val htmlFragment = new ValueDistributionResultHtmlRenderer(createRendererFactory()).render(result);
    htmlFragment.initialize(context);

    Assert.assertEquals(8, htmlFragment.getBodyElements().size());
    Assert.assertEquals(3, htmlFragment.getHeadElements().size());

    val html = htmlFragment.getBodyElements().get(7).toHtml(context);
    Assert.assertEquals("""<div class="valueDistributionResultContainer">
                 <div class="valueDistributionGroupPanel">
             <h3>Group: eobjects.dk</h3>
             <div class="valueDistributionChart" style="height: 200px;" id="reselem_1">
               </div>
             <table class="valueDistributionValueTable">
                   <tr><td>kasper</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_2'); return false;" href="#">4</a></td></tr><tr><td>kasper.sorensen</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_3'); return false;" href="#">2</a></td></tr><tr><td>&lt;null&gt;</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_4'); return false;" href="#">1</a></td></tr><tr><td>info</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_5'); return false;" href="#">1</a></td></tr>
                 </table>
             <table class="valueDistributionSummaryTable">
               <tr><td>Total count</td><td>8</td></tr>
               <tr><td>Distinct count</td><td>4</td></tr>
             </table>
           </div><div class="valueDistributionGroupPanel">
             <h3>Group: humaninference.com</h3>
             <div class="valueDistributionChart" style="height: 50px;" id="reselem_6">
               </div>
             <table class="valueDistributionValueTable">
                   <tr><td>kasper.sorensen</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_7'); return false;" href="#">1</a></td></tr><tr><td>kaspers</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_8'); return false;" href="#">1</a></td></tr><tr><td>winfried.vanholland</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_9'); return false;" href="#">1</a></td></tr>
                 </table>
             <table class="valueDistributionSummaryTable">
               <tr><td>Total count</td><td>3</td></tr>
               <tr><td>Distinct count</td><td>3</td></tr>
             </table>
           </div>
               </div>""".replaceAll("\r\n", "\n"), html.replaceAll("\r\n", "\n"));

    Assert.assertEquals("""<script type="text/javascript">
    //<![CDATA[
    var data = [
        {label:"kasper", data:[[4,-1]]},{label:"kasper.sorensen", data:[[2,-2]]},{label:"&lt;null&gt;", data:[[1,-3]]},{label:"&lt;unique&gt;", data:[[1,-4]]}
    ];
    draw_value_distribution_bar('reselem_1', data, 2);
    //]]>
</script>
""".replaceAll("\r\n", "\n"), htmlFragment.getHeadElements().get(1).toHtml(context).replaceAll("\r\n", "\n"))

    Assert.assertEquals("""<script type="text/javascript">
    //<![CDATA[
    var data = [
        {label:"&lt;unique&gt;", data:[[3,-1]]}
    ];
    draw_value_distribution_bar('reselem_6', data, 2);
    //]]>
</script>
""".replaceAll("\r\n", "\n"), htmlFragment.getHeadElements().get(2).toHtml(context).replaceAll("\r\n", "\n"))
  }

  @Test
  def testRenderSingleGroups = {
    val context = new DefaultHtmlRenderingContext();

    val col1 = new MockInputColumn[String]("email username", classOf[String]);

    val analyzer = new ValueDistributionAnalyzer(col1, true, null, null);

    analyzer.run(new MockInputRow().put(col1, "kasper"), 6);
    analyzer.run(new MockInputRow().put(col1, "kasper.sorensen"), 3);
    analyzer.run(new MockInputRow().put(col1, "kasper"), 3);
    analyzer.run(new MockInputRow().put(col1, ""), 2);
    analyzer.run(new MockInputRow().put(col1, "info"), 1);

    val result = analyzer.getResult();

    val htmlFragment = new ValueDistributionResultHtmlRenderer(createRendererFactory()).render(result);
    htmlFragment.initialize(context);

    Assert.assertEquals(5, htmlFragment.getBodyElements().size());
    Assert.assertEquals(2, htmlFragment.getHeadElements().size());

    var html: String = null;

    html = htmlFragment.getBodyElements().get(0).toHtml(context);
    Assert.assertEquals("""<div id="reselem_2" class="drillToDetailsPanel" style="display:none;">
<h3>Records (9)</h3>
<table class="annotatedRowsTable"><tr class="odd"><th>email username</th></tr><tr class="even"><td class="highlighted">kasper</td></tr><tr class="odd"><td class="highlighted">kasper</td></tr></table>
</div>""".replaceAll("\r\n", "\n"), html.replaceAll("\r\n", "\n"));

    html = htmlFragment.getBodyElements().get(1).toHtml(context);
    Assert.assertEquals("""<div id="reselem_3" class="drillToDetailsPanel" style="display:none;">
<h3>Records (3)</h3>
<table class="annotatedRowsTable"><tr class="odd"><th>email username</th></tr><tr class="even"><td class="highlighted">kasper.sorensen</td></tr></table>
</div>""".replaceAll("\r\n", "\n"), html.replaceAll("\r\n", "\n"));

    html = htmlFragment.getBodyElements().get(4).toHtml(context);
    Assert.assertEquals("""<div class="valueDistributionResultContainer">
                 <div class="valueDistributionGroupPanel">
             
             <div class="valueDistributionChart" style="height: 200px;" id="reselem_1">
               </div>
             <table class="valueDistributionValueTable">
                   <tr><td>kasper</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_2'); return false;" href="#">9</a></td></tr><tr><td>kasper.sorensen</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_3'); return false;" href="#">3</a></td></tr><tr><td>&lt;blank&gt;</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_4'); return false;" href="#">2</a></td></tr><tr><td>info</td><td><a class="drillToDetailsLink" onclick="drillToDetails('reselem_5'); return false;" href="#">1</a></td></tr>
                 </table>
             <table class="valueDistributionSummaryTable">
               <tr><td>Total count</td><td>15</td></tr>
               <tr><td>Distinct count</td><td>4</td></tr>
             </table>
           </div>
               </div>""".replaceAll("\r\n", "\n"), html.replaceAll("\r\n", "\n"));

    Assert.assertEquals("""<script type="text/javascript">
    //<![CDATA[
    var data = [
        {label:"kasper", data:[[9,-1]]},{label:"kasper.sorensen", data:[[3,-2]]},{label:"&lt;blank&gt;", data:[[2,-3]]},{label:"&lt;unique&gt;", data:[[1,-4]]}
    ];
    draw_value_distribution_bar('reselem_1', data, 2);
    //]]>
</script>
""".replaceAll("\r\n", "\n"), htmlFragment.getHeadElements().get(1).toHtml(context).replaceAll("\r\n", "\n"))
  }

  def createRendererFactory(): RendererFactory = {
    val descriptorProvider = new ClasspathScanDescriptorProvider().scanPackage("org.eobjects.analyzer.beans", true).scanPackage("org.eobjects.analyzer.result.renderer", false);
    val conf = new AnalyzerBeansConfigurationImpl().replace(descriptorProvider);
    return new RendererFactory(conf);
  }
}