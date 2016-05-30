<#--<main role="main" property="mainContentOfPage" class="container">-->
  <h1 id="wb-cont" property="name">VOSpace (${folder.name})</h1>

<#assign startTime = .now?time>
<#--<#flush>-->
  <!-- Main content -->
  <#--<section>-->
    <#--<h2>Showing ${folder.childCount} items (Started at ${startTime})</h2>-->
    <#--<pre>-->
    <#list folder.childIterator as childItem>
      <#assign uri = childItem.URI>
      <#assign writeGroupNames = childItem.writeGroupNames>
      <div class="checkbox"><label><input id="SELECT_${uri}" type="checkbox" /><span class="hidden"></span></label></div>&nbsp;&nbsp;&nbsp;&nbsp;
      <span class="glyphicon ${childItem.itemIconCSS}"></span> <a href="/beacon${childItem.linkURI}" title=""> ${childItem.name}</a>&nbsp;&nbsp;&nbsp;&nbsp;
      ${childItem.sizeHumanReadable}&nbsp;&nbsp;&nbsp;
      ${writeGroupNames}&nbsp;&nbsp;&nbsp;
      ${childItem.readGroupNames}&nbsp;&nbsp;&nbsp;
      ${childItem.lastModifiedHumanReadable}
      <br />
    </#list>      
    <#--</pre>-->
  <#--</section>-->

  <dl id="wb-dtmd">
    <dt>Date modified:&#32;</dt>
    <dd>
    <#assign endTime = .now?time>
      <time property="loadTime">${endTime}</time>
      <time property="dateModified">2016-05-18</time>
    </dd>
  </dl>
<#--</main>-->