<#assign startTime = .now?time>
<#assign isRoot = folder.root>
<main role="main" property="mainContentOfPage" class="col-md-10 col-md-push-2">
  <h1 id="wb-cont" class="wb-inv" property="name">${folder.path}</h1>
  <section>
    <h2 id="wb-cont" property="name">
    <#if isRoot>
      ROOT
    <#else>
      ${folder.path}
      &nbsp;
      <a id="level-up" name="level-up" class="btn btn-default" title="Up one level" href="/beacon/list${folder.parentPath}" role="button">
        <span class="glyphicon glyphicon-arrow-up"></span></a>
    </#if>
    </h2>
  </section>

<#flush>
  <!-- Main content -->
  <section>
    <div id="fileinfo" class="table-responsive">
      <div id="beacon_filter" class="dataTables_filter mrgn-lft-md mrgn-tp-md hidden row">
        <label>Search:<input type="search" class="" placeholder=""
                             aria-controls="beacon"/></label>
      </div>
      <table id="beacon" class="table table-striped table-condensed table-hover">
        <thead>
          <tr>
            <th></th>
            <th>Name</th>
            <th>Size</th>
            <th>Read/Write</th>
            <th>Read</th>
            <th>Last Modified (UTC)</th>
          </tr>
        </thead>
        <tbody>
        <#list folder.childIterator as childItem>
          <#assign uri = childItem.URI>
          <#assign writeGroupNames = childItem.writeGroupNames>
          <tr>
            <td class="select-checkbox"></td>
            <td><span class="glyphicon ${childItem.itemIconCSS}"></span> <a href="/beacon${childItem.linkURI}"> ${childItem.name}</a> </td>
            <td>${childItem.sizeHumanReadable}</td>
            <td>${writeGroupNames}</td>
            <td><#if childItem.public><a href="#" class="public_link" title="Change group read access.">Public</a><#else>${childItem.readGroupNames}</#if></td>
            <td>${childItem.lastModifiedHumanReadable}</td>
          </tr>
        </#list>
        </tbody>
      </table>
    </div>
  </section>

  <dl id="wb-dtmd">
    <dt>Date modified:&#32;</dt>
    <dd>
      <#assign endTime = .now?time>
      <#flush>
      <time property="loadTime">${endTime}</time>
      <time property="dateModified">${.now?date}</time>
    </dd>
  </dl>
</main>

<#--<div class="row">-->
  <#--<form id="uploader" method="post" class="mrgn-rght-md mrgn-lft-sm">-->
    <#--<h1 title="${folder.path}">${folder.path}</h1>-->
    <#--<div id="uploadresponse"></div>-->
  <#--&lt;#&ndash;<button id="home" name="home" type="button" value="Home"><span class="glyphicon glyphicon-home"></span>&nbsp;</button>&ndash;&gt;-->
    <#--<input id="mode" name="mode" type="hidden" value="add" />-->
    <#--<input id="currentpath" name="currentpath" type="hidden" value="${folder.path}"/>-->
    <#--<div id="file-input-container" class="wb-inv">-->
    <#--&lt;#&ndash;<div id="alt-fileinput">&ndash;&gt;-->
    <#--&lt;#&ndash;<input id="filepath" name="filepath" type="text" />&ndash;&gt;-->
    <#--&lt;#&ndash;<button id="browse" name="browse" type="button" class="btn" value="Browse"></button>&ndash;&gt;-->
    <#--&lt;#&ndash;</div>&ndash;&gt;-->
      <#--<input id="newfile" name="newfile" type="file" />-->
    <#--</div>-->
  <#--&lt;#&ndash;<button id="upload" name="upload" type="submit" value="Upload" class="em"><span class="glyphicon glyphicon-cloud-upload"></span>&nbsp;Upload</button>&ndash;&gt;-->

  <#--</form>-->
<#--</div>-->

<nav role="navigation" id="wb-sec" typeof="SiteNavigationElement" class="col-md-2 col-md-pull-10 visible-md visible-lg">
  <h2>Section menu</h2>
  <form id="uploader" method="post">
    <input id="currentpath" name="currentpath" type="hidden" value="${folder.path}"/>
    <ul class="list-group menu list-unstyled">
      <li>
        <h3 class="wb-navcurr">VOSpace</h3>
        <ul class="list-group menu list-unstyled">
        <li>
          <button id="newfolder" name="newfolder" type="button" class="btn btn-default btn-block text-right" <#if isRoot>disabled="disabled" title="Not permitted at ROOT level"</#if>>
            <span class="glyphicon glyphicon-folder-open"></span>&nbsp;New folder</button>
        </li>
        </ul>
      </li>
    </ul>
    <ul class="list-group menu list-unstyled">
      <li>
        <h3 class="wb-navcurr">Manage</h3>
        <ul class="list-group menu list-unstyled">
          <li><a class="btn btn-default btn-block text-right" href="/canfar/gmui">
            <span class="glyphicon glyphicon-user"></span>&nbsp;Groups</a></li>
        </ul>
      </li>
    </ul>
  </form>
</nav>

<hr class="full-width" />
