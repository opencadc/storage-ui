<main role="main" property="mainContentOfPage" class="container">
  <h1 id="wb-cont" property="name">VOSpace</h1>

<#assign startTime = .now?time>
<#flush>
  <!-- Main content -->
  <section>
    <#--<div id="loading-wrap"><!-- loading wrapper / removed when loaded &ndash;&gt;</div>-->

    <div class="row">
      <form id="uploader" method="post" class="mrgn-rght-md mrgn-lft-sm">
        <h1 title="${folder.path}">${folder.path}</h1>
        <div id="uploadresponse"></div>
        <button id="level-up" name="level-up" type="button" value="LevelUp"><span class="glyphicon glyphicon-arrow-up"></span>&nbsp;</button>
        <button id="home" name="home" type="button" value="Home"><span class="glyphicon glyphicon-home"></span>&nbsp;</button>
        <input id="mode" name="mode" type="hidden" value="add" />
        <input id="currentpath" name="currentpath" type="hidden" value="${folder.path}"/>
        <div id="file-input-container">
          <div id="alt-fileinput">
            <input id="filepath" name="filepath" type="text" />
            <button id="browse" name="browse" type="button" value="Browse"></button>
          </div>
          <input id="newfile" name="newfile" type="file" />
        </div>
        <button id="upload" name="upload" type="submit" value="Upload" class="em"><span class="glyphicon glyphicon-cloud-upload"></span>&nbsp;Upload</button>
        <button id="newfolder" name="newfolder" type="button" value="New Folder" class="em"><span class="glyphicon glyphicon-folder-open"></span>&nbsp;New folder</button>
      </form>
    </div>

    <div id="fileinfo" class="table-responsive">
      <div id="beacon_filter" class="dataTables_filter mrgn-lft-md mrgn-tp-md hidden">
        <label>Search:<input type="search" class="" placeholder=""
                             aria-controls="beacon"/></label>
      </div>
      <table id="beacon" class="table table-striped table-condensed table-hover">
        <thead>
          <tr>
            <th></th>
            <th><a href="/sort${folder.path}?dir=asc&col=Name" title="Sort Name ascending">Name</a></th>
            <th><a href="/sort${folder.path}?dir=asc&col=Size" title="Sort Size ascending">Size</a></th>
            <th>Read/Write</th>
            <th>Read Only</th>
            <th><a href="/sort${folder.path}?dir=asc&col=Last Modified" title="Sort Last Modified ascending">Last Modified (UTC)</a></th>
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
            <td>${childItem.readGroupNames}</td>
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