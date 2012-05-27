(function($) {
    $(function() {
        $('<div>').attr('style', 'display: none;')
                  .load('filechooser-classic.html')
                  .appendTo($('body'));
    });
    $.fn.fileChooser = function(options) {
        var settings = $.extend({
            title : 'File Chooser',
            url : 'ajax/filesystem/',
            separator : '/',
            selectableFile : true,
            selectableDir : false,
            filters : [ '*' ]
        }, options || {});

        var textfield$ = this.filter('input:text');
        var dialog$ = $('#filechooser-dialog');
        var dirs$ = $('#dirs', dialog$);
        var filter$ = $('#filter', dialog$);
        var currentDir$ = null;

        dialog$.dialog({
            open : function() {
                filter$.empty();
                $.each(settings.filters, function(n, filter) {
                    var value = filter;
                    if ($.isArray(filter)) {
                        value = filter.join(';');
                    }
                    $('<option>').val(value).text(value).appendTo(filter$);
                });

                querySeparator();
                $('#filename', dialog$).val(
                        filename(networkPath(textfield$.val())));
            },
            title : settings.title,
            autoOpen : false,
            modal : true,
            width : 560
        });

        $('#ok', dialog$).click(
                function() {
                    textfield$.val(concat(
                            currentDir$.val(), $('#filename', dialog$).val()));
                    close();
                });
        $('#cancel', dialog$).click(function() {
            close();
        });
        function close() {
            $('option', dialog$).filter(function() {
                return $(this).data('index') > 0;
            }).remove();
            $('select', dialog$).unbind('change');
            $('button', dialog$).unbind('click');
            $('li', dialog$).unbind('click').unbind('dblclick');
            dialog$.dialog('close');
        }

        $('#dirs', dialog$).bind('change', function(event) {
            currentDir$ = $(':selected', this);
            var path = currentDir$.val();
            var index = currentDir$.data('index');
            $.get(settings.url + "list" + path, {
                q : filter$.val()
            }, function(response) {
                $('option', dialog$).filter(function() {
                    return $(this).data('index') > index;
                }).remove();
                view_files(response);
            }, 'json');
        });

        filter$.bind('change', function(event) {
            dirs$.change();
        });

        function querySeparator() {
            $.get(settings.url + 'separator', {}, function(separator) {
                settings.separator = separator;
                queryRoots();
            }, 'text');
        }

        function queryRoots() {
            dirs$.empty();
            $.get(settings.url + "roots", {}, function(roots) {
                function createDirs(dirs) {
                    return $.map(dirs, function(dir) {
                        return $('<option>').val(networkPath(dir))
                                            .text(serverPath(dir))
                                            .data('index', 0);
                    });
                }
                $.each(createDirs(roots), function(n, root) {
                    root.attr('selected', n == 0)
                        .appendTo(dirs$);
                });

                var textDir = dirname(networkPath(textfield$.val()));
                var basedir = '';
                var insertable = $('option', dirs$).filter(function() {
                    var rootpath = $(this).val();
                    if (textDir < rootpath) {
                        return true;
                    } else if (textDir == rootpath) {
                        basedir = rootpath;
                        return true;
                    } else if (startsWith(textDir, rootpath)) {
                        basedir = rootpath;
                    }
                    return false;
                });
                var paths = splitPath(textDir, basedir);
                if (insertable.size() == 0) {
                    $.each(createDirs(paths), function(n, dir) {
                        dir.data('index', n + 1)
                           .attr('selected', n + 1 == paths.length)
                           .appendTo(dirs$);
                    });
                } else if (textDir == basedir) {
                    insertable.first().attr('selected', true);
                } else {
                    $.each(createDirs(paths), function(n, dir) {
                        dir.data('index', n + 1)
                           .attr('selected', n + 1 == paths.length)
                           .insertBefore(insertable.first());
                    });
                }

                dirs$.change();
            }, 'json');
        }

        function view_files(files) {
            $('li', dialog$).unbind('click').unbind('dblclick')
            $('ul', dialog$).empty();

            var contents = $('ul', dialog$).first();
            $.each(files, function(n, value) {
                if (settings.selectableFile) {
                    ;
                } else if (value.type == 'f') {
                    return;
                }
                $('<li>').text(value.name)
                         .addClass(value.type)
                         .appendTo($(contents))
            });
            $('li', dialog$).width(maxWidth());

            $('li', dialog$).bind('click', function(event) {
                if (settings.selectableDir) {
                    ;
                } else if ($(this).hasClass('d')) {
                    return;
                }
                $('#filename', dialog$).val($(this).text());
            });
            $('li.d', dialog$).bind(
                    'dblclick',
                    function(event) {
                        var index = currentDir$.data('index') + 1;
                        var value = concat(currentDir$.val(), $(this).text());
                        $('<option>').val(value)
                                     .text(value)
                                     .attr('selected', true)
                                     .data('index', index)
                                     .insertAfter(currentDir$);
                        dirs$.change();
                        $('#filename', dialog$).val('');
                    });

            function maxWidth() {
                var maxWidth = 0;
                $('li', dialog$).each(function() {
                    var width = $(this).width();
                    maxWidth = maxWidth < width ? width : maxWidth;
                });
                return maxWidth;
            }
        }

        function networkPath(path) {
            return path.replace(settings.separator, '/');
        }
        function serverPath(path) {
            return path.replace('/', settings.separator);
        }

        dialog$.dialog('open');

        function concat(dir, name) {
            if (dir == '/') {
                return '/' + name;
            } else {
                return dir + '/' + name;
            }
        }

        function dirname(path) {
            var dirname = '';
            var names = path.split('/');
            if (names.length > 0) {
                dirname = names.slice(0, names.length - 1).join('/');
            }
            if (dirname.length == 0) {
                dirname = '/';
            }
            return dirname;
        }

        function filename(path) {
            var filenamme = '';
            var names = path.split('/');
            if (names.length > 0) {
                filename = names[names.length - 1];
            }
            return filename;
        }

        function splitPath(path, base) {
            var paths = [];
            var names = path.substring(base.length).split('/');
            var i = 0;
            for (i = 0; i < names.length; i++) {
                paths.push(base + names.slice(0, i + 1).join('/'));
            }
            return paths;
        }

        function startsWith(longPath_, shortPath_) {
            var longPath = longPath + '/';
            var shortPath = shortPath + '/';
            if (longPath.length < shortPath.length) {
                return false;
            }
            var path = longPath.substring(0, shortPath.length);
            return path == shortPath;
        }

        return this;
    };
})(jQuery);
