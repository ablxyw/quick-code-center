var $ = layui.$;
var editor;
var flows;
var codeMirrorInstances = {};
var socket;
var version = 'lastest';
var tokenizer = new Tokenizer();

function renderCodeMirror() {
	require(['vs/editor/editor.main'], function () {
		$('[codemirror]').each(function () {
			var $dom = $(this);
			if ($dom.attr("rendered") == 'true') {
				return;
			}
			$dom.attr("rendered", true)
			var cm = monaco.editor.create(this, {
				language: 'spiderflow',
				contextmenu: false,
				minimap: {
					enabled: false
				},
				overviewRulerBorder: false,
				overviewRulerLanes: 0,
				folding: false,
				fixedOverflowWidgets: true,
				scrollbar: {
					horizontal: 'auto',
					vertical: 'hidden'
				},
				lineNumbers: 'off',
				theme: 'spiderflow',
				value: $dom.attr('data-value') || ''
			})
			// var cm = CodeMirror(this,{
			// 	mode : 'spiderflow',	//语法
			// 	theme : 'idea',	//设置样式
			// 	placeholder : $dom.attr("placeholder"),
			// 	value : $dom.attr('data-value') || '',
			// 	scrollbarStyle : 'null',	//隐藏滚动条
			// });
			// initHint(cm);
			codeMirrorInstances[$(this).attr('codemirror')] = cm;
			// cm.on('change',function(){
			// 	$dom.attr('data-value',cm.getValue());
			// 	if($dom.attr('codemirror') == 'condition'){
			// 		var $select = $('select[name="exception-flow"]');
			// 		$select.siblings("div.layui-form-select").find('dl dd[lay-value=' + $select.val() + ']').click();
			// 	}
			// 	serializeForm();
			// });
			var oldDecorations = [];
			var newDecorations = [];
			cm.onDidChangeModelContent(function () {
				var value = cm.getValue();
				$dom.attr('data-value', value);
				if ($dom.attr('codemirror') == 'condition') {
					var $select = $('select[name="exception-flow"]');
					$select.siblings("div.layui-form-select").find('dl dd[lay-value=' + $select.val() + ']').click();
				}
				serializeForm();
				try {
					tokenizer.tokenize(value, true);
					newDecorations = [];
					cm.deltaDecorations(oldDecorations, newDecorations)
					oldDecorations = newDecorations;
				} catch (e) {
					var decorations = [{
						range: new monaco.Range(1, e.span.start, 1, e.span.end),
						options: {
							hoverMessage: {
								value: e.message
							},
							inlineClassName: 'squiggly-error',
						}
					}];
					cm.deltaDecorations(oldDecorations, decorations)
					oldDecorations = decorations;
				}
			})
			codeMirrorInstances[$(this).attr('codemirror')] = cm;
		});
	});
}

function getCellData(cellId, keys) {
	var cell = editor.getModel().getCell(cellId);
	var data = [];
	var object = cell.data.object;
	for (var k in keys) {
		var key = keys[k];
		if (Array.isArray(object[key])) {
			var array = object[key];
			for (var i = 0, len = array.length; i < len; i++) {
				data[i] = data[i] || {};
				data[i][key] = array[i];
			}
		}
	}
	return data;
}

function serializeForm() {
	var $container = $(".properties-container");
	var _version = $container.data('version');
	if (_version && _version != version) {
		return;
	}
	var cellId = $container.attr('data-cellid');
	var model = editor.getModel();
	var cell = model.getCell(cellId);
	if (!cell) {
		return;
	}
	var shape = cell.data.get('shape');
	cell.data.reset({});
	$.each($(".properties-container form").serializeArray(), function (index, item) {
		var name = item.name;
		var value = item.value;
		if ($(".properties-container form *[name=" + name + "].array").length > 0) {
			var array = cell.data.get(name) || [];
			array.push(value);
			cell.data.set(name, array);
		} else {
			if (name == 'value') {
				if (cell.getValue() != value) {
					model.beginUpdate();
					try {
						cell.setValue(value);
						model.execute(new mxValueChange(model, cell, value));
					} finally {
						model.endUpdate();
					}
				}
			}
			if (name == 'lineWidth') {
				editor.graph.setCellStyles('strokeWidth', value, [cell]);
			}
			if (name == 'line-style') {
				editor.graph.setCellStyles('sharp', undefined, [cell]);
				editor.graph.setCellStyles('rounded', undefined, [cell]);
				editor.graph.setCellStyles('curved', undefined, [cell]);
				editor.graph.setCellStyles(value, 1, [cell]);
			}
			cell.data.set(name, value);
		}
	});
	$(".properties-container form [codemirror]").each(function () {
		var $dom = $(this);
		var name = $dom.attr('codemirror');
		var value = $dom.attr('data-value');
		if ($dom.hasClass("array")) {
			var array = cell.data.get(name) || [];
			array.push(value);
			cell.data.set(name, array);
		} else {
			cell.data.set(name, value);
		}
	});
	$(".properties-container form input[type=checkbox]").each(function () {
		if (this.value == 'transmit-variable') {
			if ($(this).is(":checked")) {
				editor.graph.setCellStyles('dashed', undefined, [cell]);
			} else {
				editor.graph.setCellStyles('dashed', 1, [cell]);
			}
		}
		cell.data.set(this.value, $(this).is(":checked") ? '1' : '0');
	});
	cell.data.set('shape', shape);
}

function resizeSlideBar() {
	var $dom = $(".sidebar-container");
	var height = $dom.height();
	var len = $dom.find("img").length;
	var totalHeight = len * 46;
	var w = Math.ceil(totalHeight / height);
	$dom.width(w * 50);
	$(".editor-container,.xml-container").css("left", w * 50 + "px");
	monacoLayout();
}

function validXML(callback) {
	var cell = editor.valid();
	if (cell) {
		layui.layer.confirm("检测到有箭头未连接到节点上，是否处理？", {
			title: '异常处理',
			btn: ['处理', '忽略'],
		}, function (index) {
			layui.layer.close(index);
			editor.selectCell(cell);
		}, function () {
			callback && callback();
		})
	} else {
		callback && callback();
	}
}

function monacoLayout() {
	for (var key in codeMirrorInstances) {
		codeMirrorInstances[key].layout();
	}
}

$(function () {
	$.ajax({
		url: 'spider/objects',
		type: 'post',
		dataType: 'json',
		success: function (data) {
			spiderflowGrammer.reset(data.data)
		}
	})
	$.ajax({
		url: 'spider/other',
		type: 'post',
		data: {
			id: getQueryString('id')
		},
		dataType: 'json',
		success: function (others) {
			flows = others;
		}
	})
	$.ctrl = function (key, callback, args) {
		var isCtrl = false;
		$(document).keydown(function (e) {
			if (!args) args = [];
			if (e.keyCode == 17) isCtrl = true;
			if (e.keyCode == key.charCodeAt(0) && isCtrl) {
				callback.apply(this, args);
				isCtrl = false;
				return false;
			}
		}).keyup(function (e) {
			if (e.keyCode == 17) isCtrl = false;
		});
	};
	$.ctrl('S', function () {
		$('input,textarea').blur();
		Save();
	});
	$.ctrl('Q', function () {
		$('input,textarea').blur();
		$(".btn-test").click();
	});
	resizeSlideBar();
	var templateCache = {};

	function loadTemplate(cell, model, callback) {
		serializeForm();
		var cells = model.cells;
		var template = cell.data.get('shape') || 'root';
		if (cell.isEdge()) {
			template = 'edge';
		}
		var v = version;
		var render = function () {
			layui.laytpl(templateCache[template]).render({
				data: cell.data,
				value: cell.value,
				flows: flows || [],
				model: model,
				cell: cell
			}, function (html) {
				$(".properties-container").attr('data-version', v).html(html).attr('data-cellid', cell.id);
				layui.form.render();
				renderCodeMirror();
				resizeSlideBar();
				callback && callback();
			})
		}
		if (templateCache[template]) {
			render();
			return;
		}
		$.ajax({
			url: 'resources/templates/' + template + ".html?_t" + new Date().getTime(),
			async: false,
			success: function (content) {
				templateCache[template] = content;
				render();
			}
		});
	}

	if (!mxClient.isBrowserSupported()) {
		layui.layer.msg('浏览器不支持!!');
	} else {
		editor = new SpiderEditor({
			element: $('.editor-container')[0],
			selectedCellListener: function (cell) {	//选中节点后打开属性面板
				loadTemplate(cell, editor.getModel(), serializeForm);
			}
		});
		//绑定工具条点击事件
		bindToolbarClickAction(editor);
		//加载图形
		loadShapes(editor, $('.sidebar-container')[0]);
		layui.form.on('checkbox', function (e) {
			serializeForm();
		});
		layui.table.on('tool', function (obj) {
			layui.layer.confirm('您确定要删除吗？', {
				title: '删除'
			}, function (index) {
				obj.del();
				serializeForm();
				renderCodeMirror();
				layui.layer.close(index);
			});
		})
		//节点名称输入框事件
		$("body").on("mousewheel", ".layui-tab .layui-tab-title", function (e, delta) {
			var $dom = $(this);
			var wheel = e.originalEvent.wheelDelta || -e.originalEvent.detail;
			var delta = Math.max(-1, Math.min(1, wheel));
			e.preventDefault = function () {
			}
			if (delta > 0) {
				$dom.scrollLeft($dom.scrollLeft() - 60);
			} else {
				$dom.scrollLeft($dom.scrollLeft() + 60);
			}
			return false;
		}).on("dblclick", ".layui-input-block[codemirror]", function () {
			if ($(this).parent().hasClass("layui-layer-content")) {
				return;
			}
			layui.layer.open({
				type: 1,
				title: '请输入' + $(this).prev().html() + '表达式',
				content: $(this),
				skin: 'codemirror',
				area: '800px'
			})
		}).on("blur", "input,textarea", function () {
			serializeForm();
		}).on("click", ".history-version li", function () {
			var timestamp = $(this).data("timestamp");
			layui.layer.confirm('你确定要恢复到该版本吗？', function (index) {
				layui.layer.close(index);
				var layerIndex = layui.layer.load(1);
				$.ajax({
					url: 'spider/history',
					data: {
						id: id,
						timestamp: timestamp
					},
					success: function (data) {
						if (data.code == 1) {
							version = timestamp;
							editor.setXML(data.data);
							layui.layer.close(layerIndex);
							layui.layer.msg('恢复成功')
						} else {
							layui.layer.msg(data.message);
						}
					}
				})
			});
		}).on("click", ".btn-history", function () {
			$.ajax({
				url: 'spider/history',
				data: {
					id: id
				},
				success: function (data) {
					if (data.code == 1) {
						if (data.data.length > 0) {
							var array = [];
							for (var i = data.data.length - 1; i >= 0; i--) {
								var timestamp = Number(data.data[i])
								array.push({
									time: new Date(timestamp).format('yyyy-MM-dd hh:mm:ss'),
									timestamp: timestamp
								})
							}
							layui.laytpl($('#history-version-tmpl').html()).render(array, function (html) {
								layui.layer.open({
									type: 1,
									title: '历史版本',
									id: 'history-revert',
									shade: 0,
									resize: false,
									content: html,
									offset: 'rt'
								})
							})
						} else {
							layui.layer.msg('暂无历史版本！');
						}
					} else {
						layui.layer.msg(data.message);
					}
				}
			})
		}).on("click", ".table-row-add", function () {	//添加一行
			serializeForm();
			var tableId = $(this).attr('for');
			var $table = $('#' + tableId);
			var cellId = $table.data('cell');
			var data = getCellData(cellId, $table.data('keys').split(","));
			data.push({});
			layui.table.reload(tableId, {
				data: data
			});
			renderCodeMirror();
		}).on("click", ".table-row-up", function () {	//上移
			var current = $(this).parent().parent().parent(); //获取当前<tr>
			var prev = current.prev();  //获取当前<tr>前一个元素
			if (current.index() > 0) {
				current.insertBefore(prev); //插入到当前<tr>前一个元素前
				serializeForm();
			}
			renderCodeMirror();
		}).on("click", ".table-row-down", function () {	//下移
			var current = $(this).parent().parent().parent(); //获取当前<tr>
			var next = current.next(); //获取当前<tr>后面一个元素
			if (next) {
				current.insertAfter(next);  //插入到当前<tr>后面一个元素后面
				serializeForm();
			}
			renderCodeMirror();
		}).on("click", ".editor-form-node .function-remove,.editor-form-node .cmd-remove", function () {
			var $dom = $(this).parents(".draggable");
			$dom.remove();
			serializeForm();
		}).on("click", ".editor-form-node .cookie-batch", function () {
			var tableId = $(this).attr('for');
			var $table = $('#' + tableId);
			var cellId = $table.data('cell');
			var data = getCellData(cellId, $table.data('keys').split(","));
			layui.layer.open({
				type: 1,
				title: '请输入Cookie',
				content: `<textarea id="cookies" name="cookies" placeholder="请输入Cookies，分号( ; )分隔Cookie，等于号( = )分隔name和value" autocomplete="off" class="layui-textarea"  lay-verify="required" style="height:250px"></textarea>`,
				area: '800px',
				btn: ['关闭', '设置'],
				btn2: function () {
					var cookieStr = $("#cookies").val();
					var cookieArr = cookieStr.split(";");
					var length = $(".draggable").length;
					serializeForm();
					for (var i = 0; i < cookieArr.length; i++) {
						var cookieItem = cookieArr[i];
						var index = cookieItem.indexOf("=");
						if (index < 0) {
							layer.alert('cookie数据格式错误');
							appendFlag = false;
							return;
						} else {
							data.push({
								'cookie-name': $.trim(cookieItem.substring(0, index)),
								'cookie-value': $.trim(cookieItem.substring(index + 1))
							})
						}
					}
					layui.table.reload(tableId, {
						data: data
					});
					renderCodeMirror();
					serializeForm();
				}
			})
		}).on("click", ".editor-form-node .header-batch", function () {
			var tableId = $(this).attr('for');
			var $table = $('#' + tableId);
			var cellId = $table.data('cell');
			var data = getCellData(cellId, $table.data('keys').split(","));
			layui.layer.open({
				type: 1,
				title: '请输入Header',
				content: `<textarea id="headers" name="headers" placeholder="请输入Headers，一行一个，冒号( : )分割name和value" autocomplete="off" class="layui-textarea"  lay-verify="required" style="height:250px"></textarea>`,
				area: '800px',
				btn: ['关闭', '设置'],
				btn2: function () {
					var headerStr = $("#headers").val();
					var headerArr = headerStr.split("\n");
					var length = $(".draggable").length;
					for (var i = 0; i < headerArr.length; i++) {
						var headerItem = headerArr[i];
						var index = headerItem.indexOf(":");
						if (index < 0) {
							layer.alert('header数据格式错误');
							return;
						} else {
							data.push({
								'header-name': $.trim(headerItem.substring(0, index)),
								'header-value': $.trim(headerItem.substring(index + 1))
							})
						}
					}
					layui.table.reload(tableId, {
						data: data
					});
					renderCodeMirror();
					serializeForm();
				}
			})
		}).on("click", ".editor-form-node .parameter-batch", function () {
			var tableId = $(this).attr('for');
			var $table = $('#' + tableId);
			var cellId = $table.data('cell');
			var data = getCellData(cellId, $table.data('keys').split(","));
			layui.layer.open({
				type: 1,
				title: '请输入参数',
				content: `<textarea id="paramters" name="paramters" placeholder="请输入参数，一行一个，冒号( : )、等号（ = ）、空格（  ）或tab（ \t ）分割name和value" autocomplete="off" class="layui-textarea"  lay-verify="required" style="height:250px"></textarea>`,
				area: '800px',
				btn: ['关闭', '设置'],
				btn2: function () {
					var paramterStr = $("#paramters").val();
					var paramterArr = paramterStr.split("\n");
					var length = $(".draggable").length;
					for (var i = 0; i < paramterArr.length; i++) {
						var paramterItem = paramterArr[i];
						var index = -1;
						var indexArr = [];
						indexArr.push(paramterItem.indexOf(":"));
						indexArr.push(paramterItem.indexOf("="));
						indexArr.push(paramterItem.indexOf(" "));
						indexArr.push(paramterItem.indexOf("\t"));
						for (var j = 0; j < indexArr.length; j++) {
							if (indexArr[j] >= 0) {
								if (index < 0) {
									index = indexArr[j];
								}
								index = Math.min(index, indexArr[j]);
							}
						}
						if (index < 0) {
							layer.alert('参数数据格式错误');
							return;
						} else {
							data.push({
								'parameter-name': $.trim(paramterItem.substring(0, index)),
								'parameter-value': $.trim(paramterItem.substring(index + 1))
							})
						}
					}
					layui.table.reload(tableId, {
						data: data
					});
					renderCodeMirror();
					serializeForm();
				}
			})
		}).on("click", ".editor-form-node .function-add", function () {
			var index = $(".draggable").length;
			$(this).parent().parent().before('<div id="function' + index + '" class="draggable" draggable="true" ondragstart="drag(event)" ondrop="drop(event)" ondragover="allowDrop(event)"><div class="layui-form-item layui-form-relative"><i class="layui-icon layui-icon-close function-remove"></i><label class="layui-form-label">执行函数</label><div class="layui-input-block array" codemirror="function" placeholder="执行函数"></div></div></div>');
			renderCodeMirror();
		}).on("click", ".editor-form-node .cmd-add", function () {
			var index = $(".draggable").length;
			$(this).parent().parent().before('<div id="' + index + '" class="draggable" draggable="true" ondragstart="drag(event)" ondrop="drop(event)" ondragover="allowDrop(event)"><div class="layui-form-item layui-form-relative"><i class="layui-icon layui-icon-close cmd-remove"></i><label class="layui-form-label">执行命令</label><div class="layui-input-block array" codemirror="cmd" placeholder="执行命令"></div></div></div>');
			renderCodeMirror();
		});
		layui.form.on('select(bodyType)', function (e) {
			var bodyType = $(e.elem).val();
			$(".form-body-raw,.form-body-form-data").hide();
			if (bodyType == 'raw') {
				$(".form-body-raw").show();
			}
			if (bodyType == 'form-data') {
				$(".form-body-form-data").show();
			}
			renderCodeMirror();
			serializeForm();
		});
		layui.form.on('select(targetCheck)', function (data) {
			var targetDiv = $(data.elem).attr('target-div');
			var targetValue = $(data.elem).attr('target-value');
			if (targetDiv != null) {
				if (data.elem.value == targetValue) {
					$("." + targetDiv).show();
				} else {
					$("." + targetDiv).hide();
				}
			}
		});

		layui.form.on('checkbox(targetCheck)', function (data) {
			var targetDiv = $(data.elem).attr('target-div');
			if (targetDiv != null) {
				if (data.elem.checked) {
					$("." + targetDiv).show();
				} else {
					$("." + targetDiv).hide();
				}
			}
		});
		layui.element.on('tab', function () {
			monacoLayout();
		})
		layui.form.on('select', serializeForm);
		var id = getQueryString('id');
		if (id != null) {
			$.ajax({
				url: 'spider/xml',
				async: false,
				data: {
					id: id
				},
				success: function (xml) {
					editor.setXML(xml);
				}
			})
			//editor.importFromUrl('spider/xml?id=' +  id);
		}
		editor.onSelectedCell();
	}

	/**
	 * 加载各种图形
	 */
	function loadShapes(editor, container) {
		//定义图形
		var shapes = [{
			name: 'start',
			title: '开始',
			image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABwUlEQVRYR9WXz1XCQBDGv9GD3sSDkpvYAR2IHWAF0oFsBUIFGyoQK1ArECpQK8BjcsMbnj7fRsN7hPDcCRt87nl25jez828FinNq2d0TWAINAR4XhJkbmStUrImK7+WGZeNAMBOgkd8hMUqN9H11lMl5A/x4/1BUsiCOt4mCN0BkOYDgds0L4jIxMqkahX8EEHMGoFX0NOmLtxOVcyCy7EDwXFRA4iM1skzKKs/gRd+M+SJAuwTgKTXSrWI4v/MrQBTzDkCvzAgJkxqJawOILHsQOIDyQ5wnRt5rAWiOeCPEZu+I+8RIaWQ0QGtP4DreocBuCvtSeQDvna4VgGjEaxCDsnLTeOUpO3azJAM4sWzvf3vd8bwcRowYZgBRTFfjuzXuDBNTiSxbELgut/vjIrCpy9VNk4/y0ABZYmnGc2gA966q/lAHwDQx4p3QwQG0a1owADeaAYw/gcFf5cBkQVxpjGetOGQZklDvB6EB1BtSaAD1PyEIAIk3AeLEyFjbQYMAAKiUgHkSBhlG2vpfWUojywkEF9rwrcgTqg5YBGgReBXBUWUIYpgYcduU6ixXsmwXRLaEuk/Ima+WbRLQ2fgC+RzXgT1bPk8AAAAASUVORK5CYII=',
			hidden: true,
			defaultAdd: true
		}, {
			name: 'request',
			image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAACbUlEQVRYR+2XTXLTQBCFX5sF7DALsHaYE+AbxD4ByQlITmDrBCQnGHMClBMkOUHMCRAnIOwU2JhdssCPamnkkmRJM5KTKqpgqryxpqe/7unpH0HHFRhOIXhPYCLARMUJxALEIM6TUFZdjhTfzS8NJ08EBsDUIbP6TYQ/Q4l9zvYCUKspuBBg6HMogbUQRz7ecAKo5QPBta/yHFAhNsTM5QknQLDkNwBjH8tr9twkC3nTJtsKEBgeQ/Cpp/JMjDhJQomazmgFGBleiuDdPgAkrm5DOewFECzJfZTnsslCGg1NPwSGYwqMCOJkLme54GMAvDI8HAjmG+Ljj1AuUwD754VVHCULORkaDp/ZRLOvF+6AeB3KuhRTWdI63rqmct8RBmgMnD5AJCZCLNO4JL7eA1OF2gKoxU+BlQje9lHgK1NUrjKl4OgMQcxsLVj6gFeV7wDYgDyF4IOPRXl0B4YrCA6cMsRZEsppcd/O8wgM/wP8Yx4YGWqiLOZ7rXxe1a9zEAI3yH5ZnSKuZJ902wNg56FImobLKVd7PveTAtAZgPgMYNszbrJesrz+hmeoHrh2JpUeHtgQR1oBXYno0QBAzKqNam2jMDJci+C5ywtdY+COeKEVsNUD+nFkqMVl/pAATa1ZrQfsEPLlIQHq7r+2GqYlOZsD0rGrbXW5Ah3f7olZ6xVUlWv9bqvzXgDEdwhe275hB6LcERUtt/18YBjpMFrnCSeA7fuKvWDVE1uAkqLKMJEeAGiVTC3JVxMAiV8CLIoDSV1DWooBLUqqpCpYVGjTtg4ZY03XJYBsTF8TiKrJJj8jhyAR3oaSNqh/AKendd/nwfiIAAAAAElFTkSuQmCC',
			title: '开始抓取',
			desc: '抓取静态HTML页面或者API接口，抓取结果存为resp变量中。<br/>支持方法参考命令提示。'
		}, {
			name: 'variable',
			image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAgCAYAAACYTcH3AAACe0lEQVRYR+2WMW5TQRCGv0mEoMMUKK80J0joKJBITgCUVAkniLehjQ+A9GwugLlBboA5QZITYLpnaEwXkZhBs+xu1s8vkhMDMpItudh9szP//PPP7Aor9JMVwsLqgmmV2roD7W9OTpsYe1jqzgWMJk4m9e/FO30W986nnDXZ1P2bPzsT480ws9XTE4EdlNeVk0EesCj1AOE9MKw6smffzPk9oQQOGsAPz5WXOajcP4KB9+cUTrng6SyYUici3M8DxiDRkSpnYyc+o+TcHCpnAhOFlgjb4VwCbuuipxr2R0A7T0DhSR1MT4RDb6Q8qpzYIYpS2wif/bbixk56Ram7CB+D7QyTRanDkDlVR1KMDIz5/wAMfhp4aJvPGTBNQT0DpSaQ58oDo95KdBeslKOxk06eZW6Pslc5GdaYIfrJz811U8zK6jjuyOPgxFhpWzaVkxl9eN1sso2ya3+FHYFWCtIERvlUOdmt66wJTBSqL5UHcVWOlKUH2dcjVToxuCrfTW9+/Vug5mOemUXBhLJ4IavSD473Ub5UTpLosu7y9Z9CL7ZoUWoX4ehPgYkaGfnugFYUbuquUo9FeG7rXKQhmfRtaWZyIadBFoQb10WpA4T9vJu8fjY4ROlmmkmdlrrpJmUK7Zzas0m4Nj03hZO6CEP797MRkUSfgZlrBDt37d1kwTZgYIPMJmWcOXnwYNP1UxtsJtm/a7amKYWOQverk+OQoDF2MIUXTVfO6l6UTZT/y701M9exvWZmzcxNO3F1NbPV18OZt8hNU1vCXn/w9upJmD0tl/B566MqvKo/O+3umHuB3TrC4gdHesmb1dXM4on8Hcs1M//FBP4FX81QGwO29kEAAAAASUVORK5CYII=',
			title: '定义变量',
			desc: '定义流程变量。<br/>定义变量有先后顺序，先定义变量后续可以使用，拖动可以交换变量顺序。'
		}, {
			name: 'loop',
			image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAADO0lEQVRYR9WWS2hTURCGvwnVlVjpzgdWxQeo0IWP7rQuRBQVFayLSlUQH5V2UWhzo4takOTGCF1UlOJCKyJiFxUFSwWxioguRBEV31qhVXeKSI3ajJybGxPTJDdpCtUDIeFmZv7vzpmZc4RxXjLO+vzHAH4tZZhySihzsih8JCTPCs1o/hlo0UlE2QlsA2YD0zOI3QV68dFBUD7kA+MN0KIT+U4zwg5gbkrQn8ArYELac2MyCJzClsNeELkB/DoT4SJQ6QZ6AZzFRw8TeEyr/HCeH9YyhqhEWAzUAbNc+26GaSIir7OBZAcIaCXKbaAEGAIaidJJm5jf2ZelRrwFnO0y6wlCNSF5mskpM0CdTmIyX12HAWLs4Khc90rnX/9bagBO/4FQ1hGW9+kxMgNYesEpNuUtw2zimDwqSDxhbOlupxbMUjoIyz5vAEuN0UnXqYawnB+VeBKiCTjqxjNZ6EmNNzIDlt4AqhDaCUlDUeJJiHNATaaYIwECuhClOp8WyhsuoOtRrjhta8u83BnIO2qBhpaq67EUW+4nvP/OgKWm55cBzdjSVaBEZnOzpcoUBDMlTX3tdVs0ii2rkgBx8a0pUcw2FA9h6QAwzZkHsCjlexBbpscBRoonOIqHCOhGlLNAacrLfUGoJSSXBUurAFP55kTrRylH+YwwBegzaSp6K/y6AnGG0hzgDcouwnIrLmmWXyP4uIk6J912FNP7gwjfxqwbDupiYrTjo56gPM5WhHuADicDMZYTkZdFv71HgPQuMMdtXFRpICztYwJgqSnuOpRDhOWO1yS8CqwFerBlXdEAAZ2PYg6yGUBr+paOnIR+rUXodIXbsKWxKIhkhw3gYwlB+eQ9Cf16HOGAa1iPLcdHBWHpEeCQ67sGW66lx8l+IUmdDUITITlWEERAT6Dsd326sWVLJv/cV7Lk/Da+ffiwCUpvTpD4wWO6aYNr14Ut1dl8vC+lliaKMh5DuYQP08cP8HEP5RcxKlAWIKxOETbWFraEcwF7Azhh1LyR+Szx3AYzQ+AMMTqJyEMv+/wAElEs3QxUACvdA2YqEAWeu/Oj3xG35Z2XcOZJmK/XGNoVloExFP5nMvAbItf8IXnK1DcAAAAASUVORK5CYII=',
			title: '循环',
			desc: ''
		}, {
			name: 'forkJoin',
			image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAADWElEQVRYR8WX34sVZRjHP9+tqJtA+uHMuY2gOwkSgugfCLopSLMNE6tNVDwzq6hYrLvBlhvovCNsucUWhiYaeBV40z8Q1E13UXQVnjlh3XQZ7TdGz7F1d+bMnEPie3ne93m+n3eeX+8R93jpHuszMcDjmZ++Tzwn85f+4dr1I7oxyWUmAoiCdwLnBGvAA8B11pgrZnVlXIixATqZpy0uYBaKVPNbMm95SJwDdlpM97v6ahyIsQDi4N3A+aH4eqEo9wWZaeCNItGXbSFaA0RnvVdrrFaJD8Xi4PPAbps3+6k+bwPRCqCTe8ZmZZT4bYjMq4i9Eu/0uvq0CaIRIA7eDyy3ER+KRZlXJGaAA0Wij0dBjASIgg8J8nHE14VjGdhv6PYTna2DqAXoZJ61OD2J+FCsE5wbDkkc7nV1pgqiEiDOfRSzVCceZU40xS7Mkzedil+8xqV+qrBRJM5dXmIWcazo6qON+5sAOsEnDIt14nHmecTJcl/wY+nQsG34W9kbNkFkXkIcFbzbS/TB+v07AOLgOWChVjz4FeBKkaj6ywUb2FEk+nojRBS8KDgBnCwSvT/cv+2ok/sZm+8b6rzc/6bqlqXDwdd5sUi0vSrecfACMCexvdfVD7eiN1hR5rJ2TxWJttZlbBz8p82BfqpLVWeizLsklotEj1TtP7bkh+9/kN+Bw8PyHBfgD5m3e6muVgl0Mr9s8VmR6NHaS2T+GZFtAmgVgtzfsca1kSGY4oWiq2crQ3DGO5jicmUIbsawKQkHFTAqCW3SynL8b5DNF6nKXBhU8AbUpjKMgq8KXiqT1fDTIJGeGlWGw1ki814v1WJtGd5uow2NKM68B3EQeGJg8yuwVFl+ubsyZYM6XiRaamxEwwP/RyuOg48Bp4AjRaLTVXlx94bRIF8skn5XeV1V3JVx3An+0HAcc7BIVU7F2tUIUFp2Ms9Y7R4kUXAmSAT7eolWRonf0QmbDg46ZdOT7BNgn+GtfqLVJp9jAQz6RO2jNA7+AiirY0/RVfk2bLVahWC9pyj3azIX1w+tOLicDa/KvN5LdbGVcl0jamMc32qp5TPrt0E769jM9hNdbmPf2IjaONkavG0Kni//mv0N395I1Wtj17oRTeJsEpuxc2ASkVE2/wK1YaYwksZPTgAAAABJRU5ErkJggg==',
			title: '执行结束',
			desc: ''
		}, {
			name: 'comment',
			image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAD/0lEQVRYR82WT2hcdRDHP9+XGL1YpaTZtzGEotX0UISSih78Uy8t2ksrVPBkVVBKafZtbNOjFUGIjdm3W0xVCjagXrSVglT0orQHlVrQIIoYK2hNXkwo0XowJdmR93Y3vt0k+3aDVn+w8Njf/GY+v5n5zYz4j5eate/6thu4C1gHdBjMCS4a/OSInx1x/pc+XWpUb0MA6bz1mvEkEBoPDddbM4gTBqNTGX2dBFIXIJW3TRTZL/H0MoquAt8jihTpQbTVyMwjjgUZ9dWDWBHAzdlWHN7C6IwUGOOI44Jxc/g26NM3ccWdvvUsQI+MjYh+IFXev6gieyb7dW45kGUB3JwdRjy3eMA4utDGi9P7FCS5NNyPYERBxraYjueDrA7Xnl8CkB62+8zhbOzgw0FWHzRiuFbGzdkAYjCm68Egq0/icksAXN9+AG4NhQJPDSVpEpzrm0UyYsJgezw5qwy4eStg7C/H/FCQ1UsrKe8csvZiC58i5lqL3H+pX5dXknVz9hDiTJRKxutTWT1TkV0E6Mrb7fMwhnGDiY+mMtpe72ZVeWIsG9/4eTdnBVS6nMSWyYwulJxSXjGFUw48MOHpuyTXpvL2powrgae9SbLrXjG35SrnEBuAkcDTvkWADQW7/g+Lbn8HcCDw9HKSwtXsuzk7gDgCTAeeOhYB3JztQLxfjv2STF3O2NqCrWkrcjrc+9PYNZvVbBJUeti2mcOHZblHA0/vRCFwc7YXMRJ+z8+xZuaQriQpiwqV+HgV0L+VdR8JPA1UAAYRA8CFwNOWJONl6KYBonO+fQXcCZwIPD1RAvDtJPBI7RNJeAWrAkj7dtzgKeBM4GlHBJDy7ZRg17UAcH0bAp6VOD2Z0c6KB6I/r0UI0r69bfAY4mjYKSseCD/y/3YSlsMdJu5WjINBVkMRQDpvoftPNZPRq3kFbs7WI34s29kdZPVuCcC3bgvLMNzUaCFaDUAqbxkZPjDT4rA5HN3+LsV5G8EIS2pDpbhZgKpSLIaCjA5W9YK0b/caRFNLg82oqWcYa0bzJjZXWnJVO0759p5gZzlGddtxMx6It2MgqoCVGlMF0FGw21TkC8HNoUC9gaRr2NbOO7yKEdDCC0GfplcqXIsDCXweeLonLrd0JIu/iCge/GMjmQMba9v8/28orbgnnGyLMArcXc6JceA1GWNzrXx2uU+/x13ZPmg3trbRi+gFwgyPxnKDWYn+IKM3lgtR4tDp+hbOhVmgtUbBmOB80VhQyWj4q1phbynCsemsvlwpPxIBomaVt02CxzH2AO1127WYoMhJOYxW5r568g0BVBTcUrCuhQXCLF6PQ7eg24zrgF/DMcvg7JSn0mTV4GoKoEGdTYn9Bf4L6DCOEiGKAAAAAElFTkSuQmCC',
			title: '注释',
			desc: '仅仅是注释,毫无作用'
		}, {
			name: 'output',
			image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAByUlEQVRYR+WXwVHDMBBF/4YD3AgHwEd3QKiA0IE7IOnAqoCkAqWDmAqACggVJJTAzRku4UYOmWVWloMZgi17bGcGdNGMrZWeVtrdL4JtXc3dI8I9gH76rWIfxSENXW0pHehpHoAwdTXMHccYx4pGLnNlAUYg3BojxtjFODPGB+Hmmw1jGCuKiubZCRCHtP1eNIH89zT3QXgy7IwXIlzYjRRC1A7wwTg5BGauELUDiPfkQrtCNAIg7neFaAzAFaJRAIE41dzrJHfieNfFrB2gMGoYz7GibbL7GwAmjQO9vN0zMDGh2YQHCt2eJKsZCFf/F+A3L9VyCV2OQMLxgKA3DPWmaJHatAbgaTbVloHFmnG9UrQSiDYBfFlcEpL0y5AuWwWQxc40B51EdUkzyqmUB6xs0wB8l3PfNYaBHgFdqx1UKYCs8KgKkLVjYFUKwJbYKN1BFQjjga/CNC4FUGXBrI0NxbmtinexokFrAJ5mnwlz8Z7oxjXQl1BsEyDJA4z3NeC3ngeM+4HJBgj3kgn3XgvONRs9IOe/VBTuoxa46wEQnN51pcKSMTAZNEcR1fc4zSFjxuNSUfDjCKyuezCyqanGeN0AQTYKPgFOSUMAph/CYQAAAABJRU5ErkJggg==',
			title: '输出',
			desc: '输出流程中的变量结果（仅测试下有用）'
		}, {
			name: 'collects',
			image: 'data:image/png;base64,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x7uGVpWoDwkQKVmUqw58BaTWqZ92RwS45ckF/e8HwIsuS2RVCV0f0ngcICMF8EXgRwAPLFP5a6LbSIzdwH+4LlVey7I8EWajAD6uat7oJkqnWwIo8DDwhstS9SsBHGM38LCIVe3CItsxAY6wCTwLPOWyZGEU9ZUiqGRC6qdoxP498KPOA7i7GgFMTL0AeBSHB0xE1vbVKJCBYlsHvg4Eot0N4l09PWcaRhuVbwBPuipdpEq9jwxCtci6ngKeBP0GKFWZ7upFXRHgtu0/i3NJiU4RLwmdlTLoo5VA4DYMWIEvg0yB4bM7unPd7DoeW2XhFd/G4ZIw6BM9II7adTqdvURc14iDTENdE+CW7f+DAt+/cPIl4B9wNApEVrF9oAdY9xtc/+wXoxcBtu54oeuXOcnIIMBlP1kH8AiOUstZta57zoogtOoyAcWrwBfCujsPJicEiJeEiqq+AnzNxTsVqAa9H5tSi5wmwPq6SLztu61Dy9+xcJqTRUQU+DuWkJtmKaiFoXP3qTxhFaqhM/lfBh5UdZti0BkBbnniGUQURZ4D/s3FOyPVnrYHRG7X/48Z5Blw1/vB8QgAzRTDfBUHeYZVlXoPLwfrkbOt7V3Av2aRTdEpAf5s+zOIQLEoPwJuJl6ydCV1PQx7Nj6t3v3oZYnd7/6UQvBdRJ32fljEK7jjgtcVLekjUpMdwDXA2XS4ZRVEkY2sxTe9dXKzgjasf51u1ShxaN43EV4iKJAU3NktMgnvuGvLZhZOZ+wCCgwXCrylUslkFMjSK1gEJqsBs0HkrJJd9/4BBhhggAEGGGCAAVYp/h/40MvBKuKzsAAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAyMC0wNy0xOVQwMzozOToxNSswMDowMFrQJhgAAAAldEVYdGRhdGU6bW9kaWZ5ADIwMTktMDEtMDhUMjA6MTA6NTkrMDA6MDD8Jup+AAAAIHRFWHRzb2Z0d2FyZQBodHRwczovL2ltYWdlbWFnaWNrLm9yZ7zPHZ0AAAAYdEVYdFRodW1iOjpEb2N1bWVudDo6UGFnZXMAMaf/uy8AAAAYdEVYdFRodW1iOjpJbWFnZTo6SGVpZ2h0ADYyNTiCK7gAAAAXdEVYdFRodW1iOjpJbWFnZTo6V2lkdGgANTUx4RmXYgAAABl0RVh0VGh1bWI6Ok1pbWV0eXBlAGltYWdlL3BuZz+yVk4AAAAXdEVYdFRodW1iOjpNVGltZQAxNTQ2OTc4MjU5i6xF3gAAABJ0RVh0VGh1bWI6OlNpemUAMTg0MThCuirukAAAAFp0RVh0VGh1bWI6OlVSSQBmaWxlOi8vL2RhdGEvd3d3cm9vdC93d3cuZWFzeWljb24ubmV0L2Nkbi1pbWcuZWFzeWljb24uY24vZmlsZXMvMTIxLzEyMTM4NTYucG5nQEpZWgAAAABJRU5ErkJggg==',
			title: '數據收集',
			desc: '將數據搜集到collects變量 List'
		}, {
			name: 'executeSql',
			image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAD1ElEQVRYR7WXUXIaRxCGv4YqR2+WH2zxZukEUU5gdAKjExhfILAnMD7BohMYn8D4BEYnsHQCobfd5CHkDVIFnerZmc0sLAJjMlVUSbszPX93//13r7DnOk319JcGb0TpAOfRzyxMw0+F8WLF7SyR2T6mZdemVqrnCB+A7sZe5dE9E17X2BmhfMwSMXBb11YAzmPhg0DfnVYeFcYKkz8SGddZfJVqR6At0ClBCYP5ipttEakF4C//JnCpyt/AIE9kuCta8fuzVA34QITnCncL5aoOxAaAl6leNgS7/FSVrwvo7pvPdYDOERiJ8FZhtlKu/kzkLt5XAeA9f/CX3+SJFOH/yXWW6lCEnoFYKBexQyWAtbAf7fKAPQJRSUcJoNygfM0TsVI7+jpLdWzp8NUxKAoI8KX2YIRbwPmhOd+F2HNiasREubASDQBGCO9USdbZ3kq1TRPNfpfb9Qsc8CavWfIY17sRudnk+XzJ/bozrVQHTleUz1kiXTFUJ8JfVudZIqZwbjkjwhevePZoulSuA4tLQ36/wjDvS+IjOkF4g3KVJTKpAT41nZgrL8TEoyF8UaVCvFaqbpOVooEQ4Ver57wvv7moCN8sZVKIU8eH9X2WyKiV6i4ALuIr5VpaqZb/BIWLOHGfJ3LpvXKAsr7YGRdGM2Bn1p3YBSDstzSYMYfWDIdQBQ9RbrNE2h6A9YLzLJFBlEcX4vX9uwA4e0NVs28ASs/iXLkN1uWKPFYayrEAWErFI6kQ0IAEXbC/rQmJchMIdRQA3nEHIJCrhq0mFt2ysxXt9TgpKAFsSUElHakaiE+m5XlfXhwlAua4cl9LwjpFi8l6LACBhBtl6EUo9Wo1isUlLkMj6BJmTaHnUuWrZlcVVMqwTog8gO+BG14tv7syjHTApNuGFhMVF7X/OLK/EJVSDNOsLxdR+7wz9aukw+u3CZUrIWsq0QqTzwmMnRRXX8aaUpb+1mbkgMHQZNa3zfEc+qG5mPiY/juJLuR6YvPjEjoN6LrIrIGzAWejGfn82uT7UDex1BHy0GfxxFVpxx6E03cTnbwv14de8tS5iJxOT3xkiyN+WJj4kP5/I5lyv4B2SOXmUApuYon7+89GIxr3Niau+rHcCFWAGC+U94eOaD7nn+xDxWaHFbSfHMuDp5V0wAxhkPfk5kciYWxXoedH/ErYYztPfpqd2GdZ8V1oa2oRQZjkPbGy21he1N4CNkMU453ycQ7DH/o0iy276QisQgq1q64wJ5SzZPla+WznDv44Xb/J0vIM2o1CmEwJT4NSuq5mqYLpCsb/wGRf3vwLgODoY+vqQ1gAAAAASUVORK5CYII=',
			title: '执行SQL',
			desc: '执行sql，需配置数据源，sql执行结果存于变量rs中。<br/>语句类型为：select，返回:List&lt;Map&lt;String,Object&gt;&gt;<br/>语句类型为：selectOne，返回:Map&lt;String,Object&gt;<br/>语句类型为：selectInt，返回:Integer<br/>语句类型为：insert、update、delete，返回:int，批量操作返回int数组<br/>sql中变量必须用 # # 包裹，如：#${title}#'
		}, {
			name: 'executeFlag',
			image: 'data:image/jpg;base64,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',
			title: '根据主键从数据库中顺序获取下一个行数据',
			desc: '從表中順序獲取數據，需配置数据源，sql执行结果存于变量rs中。<br/>'
		}, {
			name: 'script',
			image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAADD0lEQVRYR92XTUhUURTH//8xyzYRJM2MuZCoFrk0iD4WFQiJRSsnF4GmIFHkvEdmUIEKRZjVm5EoDErcmS4rSgpzIX1ArcIWYSZt5vkBSUWM0zgn3nPe9NSx+XBeQRcGZuaej98999xzzyX+8aDl3xuUMonhcE54iBFdYX86thIAHk1aQbSko5SmzGld4c1UsksBBG2plFLOxxdC4krIz/N/kk8GsF9XOZTSyTICHk32gXiemCZ6NnjRMOJjJJmK8wDzXgchqNdVji+G+FsAgGA05kLNpJ8v7BA5BzCMewIiy+zQLIEjIYUD1rwzAEFpgaAKQOkSEEGbrrLVUYBkq08k5/8PUCV56Ofc4iisOAKeoLRTMBZS2GU37g3InZigd0LlYFFAymOCE9EIaqfP8ZtdLmuALZ2y5rvgFgR1AEbggk9v5HvDuDcoB0Xw2HTkQili6Isn3sBcBLVTzdQtiKwAim/Lpp+z6CZQDmA4KvBNqwzZjD4EUSnE5Qk/Lxa2S1HeavSR2APgVSyCY5PN/GgeTatCZpKEnoAYRWMXiAcF6+AbP86w5dytSR2JuyA+hGPYOaNyxpgrviFroy4zEocAvNQV7l4JQC+AoySe5efD9/kkvxjGSjRZH3bhNQTbRFA/ofKeBVbSLQXhr+iDmFf7fV1hddYA8ap2FcBZAGMkfCE/37qDcomCCxA80lUaKzVHoSbeVTRXvxdAh66weUU5kFAOyBkA18zfRKXh2PxKVIT8fGKCdsp2WxI26Qqv5+QUJCCCUgPBD6PTcWtywEVUhxQ2LHJSARc26n725LwOJCuvmfyX1SnIxEE6smbbBwzZGx5HbsN0YCwZRwE8AXljONIV7lgOyjEAtyabSZhVUFeY8LMYxBEAY69dwNMYMRx3+LvJFbzTVTY6tgUeTUpAfAJgNKAlSUNvuw+ciUBQOiBoWjYZUwBk/DCJAl32WzJewo0ELMs8ApmcIUtWsOQx4w1KgwgWNC+W+Fw+tk6d4uh8ZY+PlTxOk0XAjILRAwDGJzHsHfECgGwWngudX+XU9zD0knfOAAAAAElFTkSuQmCC',
			title: '执行脚本',
			desc: '单独执行脚本方法'
		}, {
			name: 'function',
			image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACkAAAAgCAYAAACPb1E+AAAC9UlEQVRYR+2YP0zUUBjAf9+RKJPgQK6THptxERPiZOTUxEUj/tlMjCS62xoT3YARl5bJTXByBGcHISQmxhjQ6A6TPRg8JrnE62de6Z0Htne9XhMw4W1N3/u+X7//rwJgzeokygRQMs+HYSksBsr0liNrYrk6gTB3GMBiGNZ9W4al6OqiCOOqfFGYKkD1oIEVRkRwQw5l2FhyCWEMZdp3ZOqgARv6LU/1CDIPbxxZMg8rhmUxj5gcdHWw6khiJSh6+lVgAGXMd2S90/79H9czpOXpd+CsKk7FEe8fBab2whwCdeF6n/ISOFUPuLT1RFbSWLsnSMvTOVUmREJVM74tz1uVGov1C6tR9/rs2zJqefoL6FfYDpSy6SKdQDNDGkAI2yeqvKk4ci/GilMIk40aZ1w95OpIQVgWOKFQDZTLnUAzQbYCorz2HQlhW5flakmFVYFBVWYrjtiN9yEoLIkwkAa0a0jL0x8m4SLrxAKGGenqPMIDVbZrUNqfWE1Qk1AFqAc83HLkVZzrs0AGgKhQDwJG41xluVpGeB99SGKLtTx9i3LTJFUAC5u23MkFcsjTZwWYFjie5CrLUwNYRtnwHYkd+fbENHyr/eZG9als5AJphLSLqdZxL1BubzqyGFP3mkmXFNN74jtrMW8FRdgB7u4EfGiWHGXZd6QcA7iCcBEz1yQkXa7F3ID2wUeEY0HATJ9wRoVxo6SunI+N18gqCu8qtlzrVCNzaYtFVx8BVwJ4UYB5Ec4pfKrYciEOoOiqjXC1ptxv10pzcXccQBQC5VqdhaQkSGO5XN2dRWGWM13XySxKej1zBNmrBRvnYy25fyDIS1kWOdG49zNqseG92xPhcSRsqWuhgoXSn3jOFH3F71Kuaatha91RTpp7d0lhzYxQXQra3W66yO4AHL86vW+nNOpOofgI9JaZAzOAmt80p9tAmgFivlu5Aaw1ZoB2Nkglt/kHJNmSsf08lfBoU2pIy9W/14JuNCTs9e3olpRC1n8B+Qd0Dhp9ddQMugAAAABJRU5ErkJggg==',
			title: '执行函数',
			desc: '单独执行函数方法，结果不保存为变量'
		}, {
			name: 'process',
			image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAACMklEQVRYR+2WwXHTQBSGv+fMkNwIB7BvMRVgV0DoIFRAUoGlCpJUsKICnApCB4QKcCrAvslwwEebGfyYp2g1QgmytBmPOaCjtLv/t+/9+neFHT+yY31aAxw6PdwXzgVOgH5lA1OEcTqSy6Ybaw3QdZqIMKoTUOX9PJaoCUQIwEKEp7+U4fdYJmWR504He8IXYJpG8tK+9RJVP0ZhIkKSjuTKv9sOgDJLY8naUwYog6yUN4tYFq0Bek4vEM5ry6tcprFclMeYd57AcQcShCOrxjySYWsAWzTzgZlQOPoDRJkB46p4FeQAJtlc5SwIoIm56sa8cHrSEa4zTzx2sZD51o4D4YfN3QlAz2kf4evOAIoWKLetKxCShD2np2ksY9txXn7Lin6QCdsmoYkjfFgqz/Y7vBYlMXFVbuexDB6sQNdphPBOYOBNlkaSje06bZyEXrxqVBNfwfG9IMrL+6ksHASQJ2EVwIQFEt+OeybsOrWsfoUyW0P0E26MsryDtknoIXwFq9UoWmBlF8GZ+BIGVeHyxLZJWDZhHUC2+7Xy9lssH0MCJmROUQF/aplb63YfIlI3598B8AbcWQsKE8J0qQx9G3pO7Xg99WUMScJGLchD5u43hKkK0WrN5/zUGqeRnBV3gW3dCbPdwU0OUQXPINokYRPDPhjF9t8qWC5YNe4e5cpa0QigdCfcBLHxNMx+z1zcFmubhI8HqJiw8EHgnfCvSbiJdFvfN7ZgW8J+3f8AvwFIqFFyobd5IgAAAABJRU5ErkJggg==',
			title: '子流程',
			desc: '执行其他spiderFlow流程，父子流程变量共享'
		}];
		var addShape = function (shape) {
			var image = new Image();
			image.src = shape.image;
			image.title = shape.title;
			image.id = shape.name;
			image.onclick = function (ev) {
				if (shape.desc) {
					layer.tips("(" + shape.name + ")" + shape.title + "<hr/>" + shape.desc, '#' + shape.name, {
						tips: [1, '#3595CC'],
						area: ['auto', 'auto'],
						time: 4000
					});
				}
			}
			if (!shape.hidden) {
				container.appendChild(image);
			}

			if (!shape.disabled) {
				editor.addShape(shape.name, shape.title || 'Label', image, shape.defaultAdd);
			}
		}
		for (var i = 0, len = shapes.length; i < len; i++) {
			addShape(shapes[i]);
		}
		$.ajax({
			url: 'spider/shapes',
			type: 'post',
			dataType: 'json',
			async: false,
			success: function (shapeExts) {
				for (var i = 0, len = shapeExts.length; i < len; i++) {
					var shape = shapeExts[i];
					addShape(shape);
					var image = new Image();
					image.src = shape.image;
					image.title = shape.title;
					editor.addShape(shape.name, shape.title || 'Label', image, false);
					resizeSlideBar();
				}
			}
		})
	}
});

/**
 * 绑定工具条点击事件
 */
function bindToolbarClickAction(editor) {
	$(".xml-container textarea").bind('input propertychange', function () {
		editor.setXML($(this).val());
		editor.onSelectedCell();
	})
	$(".toolbar-container").on('click', '.btn-delete', function () {
		editor.deleteSelectCells();
	}).on("click", ".btn-selectAll", function () {
		editor.execute('selectAll');
	}).on('click', ".btn-undo", function () {
		editor.execute('undo');
	}).on('click', ".btn-redo", function () {
		editor.execute('redo');
	}).on('click', ".btn-cut", function () {
		editor.execute('cut');
	}).on('click', ".btn-copy", function () {
		editor.execute('copy');
	}).on('click', ".btn-paste", function () {
		editor.execute('paste');
	}).on('click', ".btn-console-xml", function () {
		console.log(editor.getXML());
	}).on('click', ".btn-edit-xml", function () {
		$(".editor-container").hide();
		$(".xml-container textarea").val(editor.getXML());
		$(".xml-container").show();
		$(this).removeClass('btn-edit-xml').addClass('btn-graphical-xml');
	}).on('click', ".btn-graphical-xml", function () {
		$(".editor-container").show();
		$(".xml-container").hide();
		$(this).removeClass('btn-graphical-xml').addClass('btn-edit-xml');
//		editor.setXML($(".xml-container textarea").val());
//		editor.onSelectedCell();
	}).on('click', '.btn-stop:not(.disabled)', function () {
		socket.send(JSON.stringify({
			eventType: 'stop'
		}));
	}).on('click', '.btn-resume:not(.disabled)', function () {
		$(this).addClass('disabled')
		$(".spiderflow-debug-tooltip").remove();
		socket.send(JSON.stringify({
			eventType: 'resume'
		}));ƒ
	}).on('click', '.btn-test', function () {
		runSpider(false);
	}).on('click', '.btn-debug', function () {
		runSpider(true);
	}).on('click', ".btn-return", function () {
		parent.openTab('配置管理', '配置管理', 'management.html')
	}).on('click', '.btn-save', function () {
		Save();
	}).on('click', '.btn-dock-right', function () {
		$('.main-container').addClass('right');
		$('.main-container .properties-container').width('40%');
		$('.main-container .resize-container').attr('style', 'top:0px;left:auto;');
		layui.table.resize('spider-variable');
		var resize = $('.resize-container')[0]
		resize.onmousedown = function (e) {
			var startX = e.clientX;
			resize.left = resize.offsetLeft;
			var box = $("body")[0];
			document.onmousemove = function (e) {
				layui.table.resize('spider-variable');
				var endX = e.clientX;
				var moveLen = resize.left + (endX - startX);
				var maxT = box.clientWidth - resize.offsetWidth;
				if (moveLen < 150) moveLen = 150;
				if (moveLen > maxT - 150) moveLen = maxT - 150;
				if (box.clientWidth - moveLen < 400 || box.clientWidth - moveLen > 800) {
					return;
				}
				resize.style.left = moveLen + 'px';
				$(".editor-container").css('right', ($('body').width() - moveLen) + 'px')
				$(".properties-container").width(box.clientWidth - moveLen - 5);
				$(".xml-container").width($(".main-container").width() - $(".properties-container").width() - $(".sidebar-container").width() + 8);
				monacoLayout();
			}
			document.onmouseup = function (evt) {
				document.onmousemove = null;
				document.onmouseup = null;
				resize.releaseCapture && resize.releaseCapture();
			}
			resize.setCapture && resize.setCapture();
			return false;
		}
		monacoLayout();
	}).on('click', '.btn-dock-bottom', function () {
		resizeSlideBar();
		$('.main-container').removeClass('right');
		$('.properties-container').height(200).width('100%');
		$('.sidebar-container').css('bottom', '200px');
		$('.editor-container').css('bottom', '200px');
		$('.main-container .resize-container').attr('style', 'left:0px;top:auto;bottom:190px');
		var resize = $('.resize-container')[0]
		resize.onmousedown = function (e) {
			var startY = e.clientY;
			resize.top = resize.offsetTop;
			var box = $("body")[0];
			var maxT = box.clientHeight;
			document.onmousemove = function (e) {
				var moveLen = e.clientY;
				if (moveLen < 250) moveLen = 250;
				if (moveLen > maxT - 150) moveLen = maxT - 150;
				resize.style.top = moveLen + 'px';
				resizeSlideBar();
				$(".editor-container,.sidebar-container,.xml-container").css('bottom', ($('body').height() - moveLen) + 'px');
				monacoLayout();
				$(".properties-container").height(box.clientHeight - moveLen - 5);
			}
			document.onmouseup = function (evt) {
				document.onmousemove = null;
				document.onmouseup = null;
				resize.releaseCapture && resize.releaseCapture();
			}
			resize.setCapture && resize.setCapture();
			return false;
		}
		monacoLayout();
	})
	$('.btn-dock-bottom').click();
}

function runSpider(debug) {
	validXML(function () {
		$(".btn-debug,.btn-test,.btn-resume").addClass('disabled');
		$(".btn-stop").removeClass('disabled');
		var LogViewer;
		var tableMap = {};
		var first = true;
		var filterText = '';
		var testWindowIndex = layui.layer.open({
			id: 'test-window',
			type: 1,
			skin: 'layer-test',
			content: '<div class="test-window-container"><div class="output-container"><div class="layui-tab layui-tab-fixed layui-tab-brief"><ul class="layui-tab-title"></ul><div class="layui-tab-content"></div></div></div><canvas class="log-container" width="845" height="100"></canvas></div>',
			area: ["850px", "430px"],
			shade: 0,
			offset: 'rt',
			maxmin: true,
			maxWidth: 900,
			maxHeight: 400,
			title: '测试窗口',
			btn: ['关闭', '显示/隐藏输出', '显示/隐藏日志', '停止'],
			btn2: function () {
				var $output = $(".test-window-container .output-container");
				var $log = $(".test-window-container .log-container");
				if ($output.is(":hidden")) {
					$output.show();
					$output.find("canvas").each(function () {
						if ($log.is(":hidden")) {
							this.height = 290;
						} else {
							this.height = 200;
						}
					})
					$log.attr('height', 100)
					LogViewer.resize();
					for (var tableId in tableMap) {
						tableMap[tableId].instance.resize();
					}
				} else {
					$output.hide();
					$log.attr('height', 320);
					LogViewer.resize();
					for (var tableId in tableMap) {
						tableMap[tableId].instance.resize();
					}
				}
				return false;
			},
			btn3: function () {
				var $output = $(".test-window-container .output-container");
				var $log = $(".test-window-container .log-container");
				if ($log.is(":hidden")) {
					$log.show();
					$log.attr('height', $output.is(":hidden") ? 320 : 100)
					$output.find("canvas").each(function () {
						this.height = 200;
					});
					LogViewer.resize();
					for (var tableId in tableMap) {
						tableMap[tableId].instance.resize();
					}
				} else {
					$log.hide();
					$output.find("canvas").each(function () {
						this.height = 320;
					});
					LogViewer.resize();
					for (var tableId in tableMap) {
						tableMap[tableId].instance.resize();
					}
				}
				return false;
			},
			btn4: function () {
				var $btn = $("#layui-layer" + testWindowIndex).find('.layui-layer-btn3');
				if ($btn.html() == '停止') {
					socket.send(JSON.stringify({
						eventType: 'stop'
					}));
				} else {
					$(".btn-debug,.btn-test,.btn-resume").addClass('disabled');
					$(".btn-stop").removeClass('disabled');
					socket.send(JSON.stringify({
						eventType: debug ? 'debug' : 'test',
						message: editor.getXML()
					}));
					$btn.html('停止');
				}
				return false;
			},
			end: function () {
				if (socket) {
					socket.close();
					$(".spiderflow-debug-tooltip").remove();
					$(".btn-stop,.btn-resume").addClass('disabled');
					$(".btn-test,.btn-debug").removeClass('disabled')
				}
				if (LogViewer) {
					LogViewer.destory();
				}
				for (var tableId in tableMap) {
					tableMap[tableId].instance.destory();
				}
			},
			success: function (layero, index) {
				var logElement = $(".test-window-container .log-container")[0];
				var colors = {
					'array': '#2a00ff',
					'object': '#2a00ff',
					'boolean': '#600100',
					'number': '#000E59'
				}
				LogViewer = new CanvasViewer({
					element: logElement,
					onClick: function (e) {
						onCanvasViewerClick(e, '日志');
					}
				});
				$(layero).find(".layui-layer-btn")
					.append('<div class="layui-inline"><input type="text" class="layui-input" placeholder="输入关键字过滤日志"/></div>')
					.on("keyup", "input", function () {
						LogViewer.filter(this.value);
					});
				socket = createWebSocket({
					onopen: function () {
						socket.send(JSON.stringify({
							eventType: debug ? 'debug' : 'test',
							message: editor.getXML()
						}));
					},
					onmessage: function (e) {
						var event = JSON.parse(e.data);
						var eventType = event.eventType;
						var message = event.message;
						if (eventType == 'finish') {
							$(".spiderflow-debug-tooltip").remove();
							$("#layui-layer" + testWindowIndex).find('.layui-layer-btn3').html('重新开始');
							$(".btn-stop,.btn-resume").addClass('disabled');
							$(".btn-test,.btn-debug").removeClass('disabled')
						} else if (eventType == 'output') {
							var tableId = 'output-' + message.nodeId;
							var $table = $('#' + tableId);
							if ($table.length == 0) {
								tableMap[tableId] = {
									index: 0
								};
								var $tab = $(".test-window-container .output-container .layui-tab")
								var outputTitle = '输出-' + tableId;
								var cell = editor.getModel().cells[message.nodeId];
								if (cell) {
									outputTitle = cell.value;
								}
								if (first) {
									$tab.find(".layui-tab-title").append('<li  class="layui-this">' + outputTitle + '</li>');
									$tab.find(".layui-tab-content").append('<div class="layui-tab-item layui-show" data-output="' + tableId + '"></div>');
									first = false;
								} else {
									$tab.find(".layui-tab-title").append('<li>' + outputTitle + '</li>');
									$tab.find(".layui-tab-content").append('<div class="layui-tab-item" data-output="' + tableId + '"></div>');
								}
								$table = $('<canvas width="845" height="200"/>').appendTo($(".test-window-container .output-container .layui-tab-item[data-output=" + tableId + "]"));
								$table.attr('id', tableId);
								tableMap[tableId].instance = new CanvasViewer({
									element: document.getElementById(tableId),
									grid: true,
									header: true,
									style: {
										font: 'bold 13px Consolas'
									},
									onClick: function (e) {
										onCanvasViewerClick(e, '表格');
									}
								})
								var cols = [];
								var texts = [new CanvasText({
									text: '序号',
									maxWidth: 100
								})];
								for (var i = 0, len = message.outputNames.length; i < len; i++) {
									texts.push(new CanvasText({
										text: message.outputNames[i],
										maxWidth: 200,
										click: true
									}));
								}
								tableMap[tableId].instance.append(texts);
							}
							var texts = [new CanvasText({
								text: ++tableMap[tableId].index,
								maxWidth: 200,
								click: true
							})];
							for (var i = 0, len = message.outputNames.length; i < len; i++) {
								var displayText = message.values[i];
								var variableType = 'string';
								if (Array.isArray(displayText)) {
									variableType = 'array';
									displayText = JSON.stringify(displayText);
								} else {
									variableType = typeof displayText;
									if (variableType == 'object') {
										displayText = JSON.stringify(displayText);
									}
								}
								texts.push(new CanvasText({
									text: displayText,
									maxWidth: 200,
									color: colors[variableType] || 'black',
									click: true
								}));
							}
							tableMap[tableId].instance.append(texts);
							tableMap[tableId].instance.scrollTo(-1);
						} else if (eventType == 'log') {
							var texts = [];
							var defaultColor = message.level == 'error' ? 'red' : '';
							texts.push(new CanvasText({
								text: message.level,
								color: defaultColor
							}));
							texts.push(new CanvasText({
								text: event.timestamp,
								color: defaultColor
							}));
							var temp = message.message.split("{}");
							message.variables = message.variables || [];
							for (var i = 0, len = temp.length; i < len; i++) {
								if (temp[i] != '') {
									texts.push(new CanvasText({
										text: temp[i],
										color: defaultColor
									}))
								}
								var object = message.variables[i];
								if (object != undefined) {
									var variableType = '';
									var displayText = object;
									if (Array.isArray(object)) {
										variableType = 'array';
										displayText = JSON.stringify(displayText);
									} else {
										variableType = typeof object;
										if (variableType == 'object') {
											displayText = JSON.stringify(displayText);
										}
									}
									texts.push(new CanvasText({
										text: displayText,
										maxWidth: 330,
										color: colors[variableType] || '#025900',
										click: true
									}))
								}
							}
							LogViewer.append(texts);
							LogViewer.scrollTo(-1);
						} else if (eventType == 'debug') {
							$(".btn-resume").removeClass('disabled');
							var type = message.event;
							editor.selectCell(editor.graph.model.cells[message.nodeId]);
							var selector;
							if (type == 'request-parameter') {
								$('.layui-tab-title li:eq(1)').click();
							}
							if (type == 'request-cookie') {
								$('.layui-tab-title li:eq(2)').click();
							}
							if (type == 'request-header') {
								$('.layui-tab-title li:eq(3)').click();
							}
							if (type == 'request-body') {
								$('.layui-tab-title li:eq(4)').click();
							}
							if (type == 'common' || type == 'request-parameter' || type == 'request-header' || type == 'request-cookie') {
								selector = '.layui-table-cell input[value=' + message.key + ']';
							}
							if ($(selector).length == 0) {
								selector = '.properties-container input[name=' + message.key + ']';
							}
							if ($(selector).length == 0) {
								selector = '.properties-container [codemirror=' + message.key + ']';
							}
							var o1 = $(selector).offset();
							var $parent = $(".properties-container");
							var o2 = $parent.offset();
							if (o1.top > o2.top + $parent.height()) {
								$parent[0].scrollTop = o1.top - o2.top;
							}
							var msg = message.value;
							var isJson = Array.isArray(msg) || typeof msg == 'object';
							if (!isJson) {
								var temp = document.createElement("div");
								(temp.textContent != null) ? (temp.textContent = msg) : (temp.innerText = msg);
								msg = temp.innerHTML;
								temp = null;
							}
							var content = '<div class="message-content" style="padding:30px;' + (isJson ? '' : 'font-weight: bold;font-family:Consolas;font-size:12px;') + '">' + (isJson ? '' : msg.replace(/\n/g, '<br>')).replace(/ /g, '&nbsp;').replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;') + '</div>';
							var tooltip = bindTooltip(content, selector);
							if (isJson) {
								var $dom = $(tooltip.dom).find(".message-content");
								jsonTree.create(msg, $dom[0]);
							}
						}
					}
				});
			}
		})
	});
}

function bindTooltip(content, selector) {
	var dom = document.createElement('div');
	var $target = $(selector);
	var offset = $target.offset();
	dom.className = 'spiderflow-debug-tooltip';
	dom.style.bottom = ($("body").height() - offset.top) + 'px';
	dom.style.left = (offset.left + $target.width() / 2) + 'px';
	dom.innerHTML = '<div class="content">' + content + '</div>';
	document.body.appendChild(dom);
	$(selector).offset();
	return {
		dom: dom,
		close: function () {
			document.body.removeChild(dom);
		}
	}

}

//最近点击打开的弹窗
var index;

function onCanvasViewerClick(e, source) {
	var msg = e.text;
	var json;
	try {
		json = JSON.parse(msg);
		if (!(Array.isArray(json) || typeof json == 'object')) {
			json = null;
		}
	} catch (e) {

	}
	if (!json) {
		var temp = document.createElement("div");
		(temp.textContent != null) ? (temp.textContent = msg) : (temp.innerText = msg);
		msg = temp.innerHTML;
		temp = null;
	}
	layer.close(index);
	index = layer.open({
		type: 1,
		title: source + '内容',
		content: '<div class="message-content" style="padding:10px;' + (json ? '' : 'font-weight: bold;font-family:Consolas;font-size:12px;') + '">' + (json ? '' : msg.replace(/\n/g, '<br>')).replace(/ /g, '&nbsp;').replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;') + '</div>',
		shade: 0,
		area: json ? ['700px', '500px'] : 'auto',
		maxmin: true,
		maxWidth: (json ? undefined : 700),
		maxHeight: (json ? undefined : 400),
		success: function (dom, index) {
			var $dom = $(dom).find(".message-content");
			if (json) {
				jsonTree.create(json, $dom[0]);
			}
		}
	});
}

function createWebSocket(options) {
	options = options || {};
	var socket;
	if (location.host === 'demo.spiderflow.org') {
		socket = new WebSocket(options.url || 'ws://49.233.182.130:8088/ws');
	} else {
		socket = new WebSocket(options.url || (location.origin.replace("http", 'ws') + '/ws'));
	}
	socket.onopen = options.onopen;
	socket.onmessage = options.onmessage;
	socket.onerror = options.onerror || function () {
		layer.layer.msg('WebSocket错误');
	}
	return socket;
}

var flowId;

function Save() {
	validXML(function () {
		$.ajax({
			url: 'spider/save',
			type: 'post',
			data: {
				id: getQueryString('id') || flowId,
				xml: editor.getXML(),
				name: editor.graph.getModel().getRoot().data.get('spiderName') || '未定义名称',
			},
			success: function (id) {
				flowId = id;
				layui.layer.msg('保存成功', {
					time: 800
				}, function () {
					// location.href = "spiderList.html";
				})
			}
		})
	});
}

function allowDrop(ev) {
	ev.preventDefault();
}

function drag(ev) {
	ev.dataTransfer.setData("moverTarget", ev.target.id);
}

function drop(ev) {
	var moverTargetId = ev.dataTransfer.getData("moverTarget");
	$(ev.target).parents(".draggable").before($("#" + moverTargetId));
	ev.preventDefault();
	serializeForm();
}
