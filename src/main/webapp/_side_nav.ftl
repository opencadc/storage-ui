<div role="navigation" class="col-sm-3 col-md-2 sidebar">
  <ul class="nav nav-sidebar">
    <li><a href="http://www.canfar.phys.uvic.ca/vosui">&nbsp;Legacy VOSpace Browser</a></li>
    <li><a href="http://www.canfar.phys.uvic.ca/canfar/groups">&nbsp;Manage Groups</a></li>
  </ul>

  <ul class="nav nav-sidebar">
    <li><a class="github-link social-link" href="http://www.github.com/opencadc/vosui"><span>GitHub</span></a></li>
    <li><a class="docker-link social-link" href="https://hub.docker.com/r/opencadc/storage/"><span>Docker</span></a></li>
  </ul>

  <form id="uploader" method="post" class="hidden">
    <h1 title="${folder.path}" class="hidden">${folder.path}</h1>
    <div id="uploadresponse"></div>
    <input id="mode" name="mode" type="hidden" value="add" />
    <input id="currentpath" name="currentpath" type="hidden" value="${folder.path}"/>
    <div id="file-input-container" class="wb-inv">
      <div id="alt-fileinput">
        <input id="filepath" name="filepath" type="text" title="File path." />
        <button id="browse" name="browse" type="button" class="btn" value="Browse"></button>
      </div>
      <input id="newfile" name="newfile" type="file" webkitdirectory="" directory="" mozdirectory="" />
    </div>
    <button id="upload" name="upload" type="submit" value="Upload" class="em">
      <span class="glyphicon glyphicon-cloud-upload"></span>&nbsp;Upload</button>
    <input id="currentpath" name="currentpath" type="hidden" value="${folder.path}"/>
  </form>
</div>