<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>jmana analyzer</title>

<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>

<link href="css/base.css" rel="stylesheet" type="text/css">

<script type="text/javascript">
var fileTotalCount, readyFileCount, successFileCount, failFileCount;

$(function() {
	$('#manga_check_all').click(function(){
		$('.list_check').prop('checked', this.checked);
	});
});

function loadMangaList() {
	var list_base_url = $('#list_base_url').val();
	var page_base_url = $('#page_base_url').val();
	var manga_title = $('#manga_title').val();
	
	if(manga_title == '') {
		alert('Input Manga Name');
		return;
	}
	
	manga_title = manga_title.split('%20').join(' ');
	$('#manga_title').val(manga_title);
	
	$.ajax({
		type		: 'post',
		url			: 'LoadMangaList',
		dataType	: 'json',
		async		: true,
		cache		: false,
		beforeSend	: function() { $('#loading').show(); },
		complete	: function() { $('#loading').hide(); },
		data		: {'list_base_url':list_base_url, 'page_base_url':page_base_url, 'manga_title':manga_title},
		success	: function(data) {
			if(data.RESULT == 'SUCCESS') {
				createMangaList(data.DATA1, data.DATA2);
			}
			else {
				alert(data.RESULT);
			}
		}
	});
	
}

function createMangaList(data, db) {
	$('#fileListStack').html('');
	
	var htmlStr = '';
	
	for(var i=0; i<data.length; i++) {
		var title = data[i].title;
		var vol = data[i].vol;
		var url = data[i].url;
		var containDB = false;
		
		htmlStr += '<tr>';
		
		for(var j=0; j<db.length; j++) {
			if(vol == db[j].vol) {
				containDB = true;
				break;
			}
		}
		
		if(containDB) htmlStr += '<td><input class="list_check" type="checkbox"/></td>';
		else htmlStr += '<td><input class="list_check" type="checkbox" checked/></td>';
		
		htmlStr += '<td>'+ title + '</td>';
		htmlStr += '<td>'+ vol + '</td>';
		htmlStr += '<td>' + url + '</td>';
		htmlStr += '</tr>';
	}

	$('#contents').html(htmlStr);
	$('#manga_check_all').prop('checked', 'checked');
}

function analysisPage() {
	$('#fileListStack').html('');
	$('#status').html('');
	$('#loading_msg').html('');
	
	var page_base_url = $('#page_base_url').val();
	
	var trs = $('#contents').find('tr');
	
	var volStack = new Array();
	
	var check_len = 0;
	
	for(var i=0; i<trs.length; i++) {
		var tds = trs.eq(i).find('td');
		
		var check = tds.eq(0).find('input').prop('checked');

		if(check) {
			var manga_title = tds.eq(1).html();
			var manga_vol = tds.eq(2).html();
			var manga_url = tds.eq(3).html();
			
			volStack.push(new Array(manga_title, manga_vol, page_base_url, manga_url));
		}
	}
	
	loadMangaPage(volStack, 0);
}

function loadMangaPage(volStack, i) {
	setTimeout(function() {
		$.ajax({
			type		: 'post',
			url			: 'LoadMangaPage',
			dataType	: 'json',
			async		: true,
			cache		: false,
			beforeSend	: function() { $('#loading').show(); },
			data		: {'manga_title':volStack[i][0], 'manga_vol':volStack[i][1], 'page_base_url':volStack[i][2], 'manga_url':volStack[i][3]},
			success	: function(data) {
				if(data.RESULT == 'SUCCESS') {
					createMangaPage(data.DATA);
					$('#loading_msg').html('Processing : '+(i+1)+' / '+(volStack.length+1));
				}
				else {
					alert(data.RESULT);
				}
			},
			complete	: function() {
				if(i != volStack.length-1) {
					i++;
					loadMangaPage(volStack, i);
				}
				else {
					$('#loading').hide();
				}
			}
		});
	}, 1);
}

function createMangaPage(data) {
	var htmlStr = '';
	
	for(var i=0; i<data.length; i++) {
		var title = data[i].title;
		var vol = data[i].vol;
		var page = data[i].page;
		var url = data[i].url;
		
		htmlStr += '<tr>';
		htmlStr += '<td><font class="font_green">Ready</font></td>';
		htmlStr += '<td>' + title + '</td>';
		htmlStr += '<td>' + vol + '</td>';
		htmlStr += '<td>' + page + '</td>';
		htmlStr += '<td>' + url + '</td>';
		htmlStr += '</tr>';
	}
	
	$('#fileListStack').append(htmlStr);
}

function fileDownload() {
	var root = $('#root').val();
	
	var trs = $('#fileListStack').find('tr');
	
	var downloadStack = new Array();

	fileTotalCount = 0;
	readyFileCount = 0;
	successFileCount = 0;
	failFileCount = 0;
	
	for(var i=0; i<trs.length; i++) {
		var tr = trs.eq(i);
		var tds = tr.find('td');
		if(tds.eq(0).text()!='SUCCESS') {
			var title = tds.eq(1).text();
			var vol = tds.eq(2).text();
			var fileName = tds.eq(3).text() + '.png';
			var url = tds.eq(4).text();
			
			fileTotalCount++;
			readyFileCount++;
			
			downloadStack.push(new Array(i, root, title, vol, fileName, url));
		}
	}

	for(var i=0; i<downloadStack.length; i++) {
		ajaxFileDownload(downloadStack[i][0], downloadStack[i][1], downloadStack[i][2], downloadStack[i][3], downloadStack[i][4], downloadStack[i][5]);
	}
}

function ajaxFileDownload(idx, root, title, vol, fileName, url) {
	var htmlStr = '';
	
	$.ajax({
		type		: 'post',
		url			: 'FileDownload',
		dataType	: 'json',
		async		: true,
		cache		: false,
		beforeSend	: function() { $('#loading').show(); },
		data		: {'root':root, 'title':title,'vol':vol, 'fileName':fileName, 'url':url},
		success		: function(data) {
			if(data.RESULT=='SUCCESS') {
				htmlStr = '<font class="font_blue">' + data.RESULT + '</font>';
				successFileCount++;
			}
			else {
				htmlStr = '<font class="font_red">DL Fail</font>';
				failFileCount++;
			}
		},
		error		: function(request, status, error) {
			htmlStr = '<font class="font_red">AJAX Fail</font>';
			failFileCount++;
		},
		complete	: function() {
			$('#loading').hide();
			
			$('#fileListStack').find('tr').eq(idx).find('td').eq(0).html(htmlStr);
			
			readyFileCount--;
			
			var readyHtmlStr = '<font class="font_green">Ready : ' + readyFileCount + '/' + fileTotalCount + '</font>';
			var successHtmlStr = '<font class="font_blue">Success : ' + successFileCount + '</font>';
			var failHtmlStr = '<font class="font_red">Fail : ' + failFileCount + '</font>';
			
			$('#status').html(readyHtmlStr+'&nbsp;&nbsp;'+successHtmlStr+'&nbsp;&nbsp;'+failHtmlStr);
			
			if(readyFileCount == 0) {
				checkFailVol();
				saveDBLog();
			}
		}
	});
}

function checkFailVol() {
	var trs = $('#fileListStack').find('tr');
	
	for(var i=0; i<trs.length; i++) {
		var tr = trs.eq(i);
		var tds = tr.find('td');
		if(tds.eq(0).text()!='SUCCESS') {
			var vol = tds.eq(2).text();
			
			var vol_trs = $('#contents').find('tr');
			
			for(var j=0; j<vol_trs.length; j++) {
				var target_name = vol_trs.eq(j).find('td').eq(2).html();
				if(target_name == vol) {
					vol_trs.eq(j).find('td').eq(0).find('input').prop('checked', false);
					break;
				}
			}
		}
	}
}

function saveDBLog() {
	var dbSuccessCnt = 0;
	var dbFailCnt = 0;
	var checkedCnt = 0;
	
	$('#contents > tr').each(function() {
		var tds = $(this).find('td');
		var check = tds.eq(0).find('input').prop('checked');

		if(check) {
			checkedCnt++;
			var manga_title = tds.eq(1).html();
			var manga_vol = tds.eq(2).html();
			
			$.ajax({
				type		: 'post',
				url			: 'SaveDB',
				dataType	:	'json',
				async		: false,
				cache		: false,
				beforeSend	: function() { $('#loading').show(); },
				complete	: function() { $('#loading').hide(); },
				data		: {'title':manga_title, 'vol':manga_vol},
				success	: function(data) {
					if(data.RESULT == 'SUCCESS') {
						dbSuccessCnt++;
					}
					else {
						dbFailCnt++;
					}
				}
			});
		}
	});
	
	alert('Database Saved\n\nAll : '+checkedCnt+'\nSuccess : '+dbSuccessCnt+'\nFail : '+dbFailCnt);
}

</script>

</head>
<body>

<table>
	<tr>
		<td>Base URL for List : </td>
		<td><input type='text' id='list_base_url' value='https://jmana18.com/book/'/></td>
		<td>Base URL for Page : </td>
		<td><input type='text' id='page_base_url' value='https://jmangahide.com/book_frame/'/></td>
		<td>Manga Name : </td>
		<td><input type='text' id='manga_title' value=''/></td>
		<td><button onclick='loadMangaList();'>Load Manga List</button></td>
		<td><button onclick='analysisPage();'>Analysis Page</button></td>
	</tr>
</table>
<table>
	<tr>
		<td style='border:1px solid #999;'>
			<input id='manga_check_all' type='checkbox'/>Check All
		</td>
		<td width='10'>
		<td><input type='text' id='root'value='C:\JMANA'/></td>
	</tr>
</table>

<div style='position:absolute; left:0px; top:80px; width:900px; height:850px; overflow-y:scroll;'>
	<table id='list_table'>
		<thead>
			<tr>
				<th style='width:50px;'>Check</th>
				<th style='width:200px;'>Title</th>
				<th style='width:300px;'>Volumn</th>
				<th style='width:350px;'>Url</th>
			</tr>
		</thead>
		
		<tbody id='contents'>
		</tbody>
	</table>
</div>

<div id='stack_div' style='position:absolute; left:900px; top:80px; width:1000px; height:850px; overflow-y:scroll; background-color:#ffffff;'>
	<table>
		<tr>
			<td><button onclick='fileDownload();'>download</button></td>
			<td id='status' style='background-color:#aaaaaa;'></td>
		</tr>
	</table>
	
	<table>
		<thead>
			<tr>
				<th style='width:50px;'>Status</td>
				<th style='width:200px;'>Title</td>
				<th style='width:300px;'>Volumn</td>
				<th style='width:50px;'>Page</td>
				<th style='width:400px;'>Url</td>
			</tr>
		</thead>
		<tbody id='fileListStack'>
		</tbody>
	</table>
</div>

<div id='loading' style='display:none; position:absolute; top:0px; left:0px; width:100%; height:100%;'>
	<div style='position:absolute; top:0px; left:0px; width:100%; height:100%; background-color:#999; opacity:0.2;'></div>
	<table cellpadding='0' cellspacing='0' style='position:absolute; top:0px; left:0px; width:100%; height:100%;'>
		<tr></tr>
		<tr height='100'><td align='center' style='background-color:#999;'><img src='images/loading.gif'></td></tr>
		<tr height='50'><td id='loading_msg' align='center' class='font_blue' style='background-color:#999;'>Loading...</td></tr>
		<tr></tr>
	</table>
</div>

</body>
</html>