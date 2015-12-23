<script id="${id}" type="text/html">
    <div class="col-lg-12">
        
        {{if is_edit_order}}
            <h4>{{label}}</h4>
            <div class="form-group button-bar" >
                <button id="add_row_btn_{{id}}" table_id="table_{{id}}" name="addRowBtn" type="button" class="btn btn-success btn-xs">添加</button>
            </div>
        {{/if}}
        <table id="table_{{id}}" name="{{name}}" class="display" cellspacing="0" style="width: 100%;">
            <thead class="eeda">
                <tr>
                    <th></th>
                    {{each field_list as field}}
                        <th>{{field.FIELD_DISPLAY_NAME}}</th>
                    {{/each}}
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
    <script>
        var table_{{id}}_setting = {
            paging: false,
            "info": false,
            "processing": true,
            "searching": false,
            "autoWidth": true,
            "language": {
                "url": "/yh/js/plugins/datatables-1.10.9/i18n/Chinese.json"
            },
            "createdRow": function ( row, data, index ) {
                var id='';
                if(data.ID){
                    id=data.ID;
                }
                $(row).attr('id', id);
                $(row).append('<input type="hidden" name="id" value="' + id + '"/>');
            },
            "columns": [
                { "width": "30px", "orderable":false, 
                    "render": function ( data, type, full, meta ) {
                      return '<a class="delete"  table_id="table_{{id}}" href="javascript:void(0)" title="删除"><i class="glyphicon glyphicon-remove"></i> </a>&nbsp;';
                    }
                },
                {{each field_list as field}}
                    { "data": "F{{field.ID}}_{{field.FIELD_NAME}}",
                        "render": function ( data, type, full, meta ) {
                            if(!data)
                                data = '';
                          return '<input type="text" name="F{{field.ID}}_{{field.FIELD_NAME}}" value="' + data + '" class="form-control"/>';
                        }
                    },
                {{/each}}
            ]
        }

        var table_{{id}}_row = {
            {{each field_list as field}}
                F{{field.ID}}_{{field.FIELD_NAME}}: '',
            {{/each}}
        };
    </script>
</script>