$(function () {
    
    var colors = Highcharts.getOptions().colors,
        categories = ['外网预审', '咨询', '受理', '补办', '办结'],
        name = '', 
        data = [{
                y:30.11,
                color: colors[0] 
            }, {
                y: 789.63,
                color: colors[1] 
            }, {
                y: 155.94,
                color: colors[2] 
            }, {
                y: 2123.15,
                color: colors[3] 
            }, {
                y: 1222.14,
                color: colors[4] 
            }],
		    data2 = [{
                y:8.11,
                color: colors[0] 
            }, {
                y: 121.63,
                color: colors[1] 
            }, {
                y: 11.94,
                color: colors[2] 
            }, {
                y: 7.15,
                color: colors[3] 
            }, {
                y: 85.14,
                color: colors[4] 
            }],
			data3 = [{
                y:91.11,
                color: colors[0] 
            }, {
                y: 21.63,
                color: colors[1] 
            }, {
                y: 11.94,
                color: colors[2] 
            }, {
                y: 12.15,
                color: colors[3] 
            }, {
                y: 2.14,
                color: colors[4] 
            }];

    function setChart(name, categories, data) { alert(data);
	chart.xAxis[0].setCategories(categories, false);
	chart.series[0].remove(false);
	chart.addSeries({
		name: name,
		data: data 
	}, false);
	chart.redraw();
    }

    var chart = $('#container').highcharts({
        chart: {
            type: 'column'
        },
        
        xAxis: {
            categories: categories
        },
        yAxis: {
            title: {
                
            }
        },
        plotOptions: {
            column: {
                cursor: 'pointer', 
                dataLabels: {
                    enabled: true,
                    color: colors[0],
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y +'';
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                s = this.x +':<b>'+ this.y +'</b>'; 
                return s;
            }
        },
        series: [{
            name: name,
            data: data,
            color: 'white'
        }],
        exporting: {
            enabled: false
        }
    })
    .highcharts(); // return chart

	var chart = $('#container2').highcharts({
        chart: {
            type: 'column'
        },
        
        xAxis: {
            categories: categories
        },
        yAxis: {
            title: {
                
            }
        },
        plotOptions: {
            column: {
                cursor: 'pointer', 
                dataLabels: {
                    enabled: true,
                    color: colors[0],
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y +'';
                    }
                }
            }
        },
        tooltip: {
            formatter: function() { 
				s = this.x +':<b>'+ this.y +'</b>'; 
                return s;
            }
        },
        series: [{
            name: name,
            data: data2,
            color: 'white'
        }],
        exporting: {
            enabled: false
        }
    })
    .highcharts();

	var chart = $('#container3').highcharts({
        chart: {
            type: 'column'
        },
        
        xAxis: {
            categories: categories
        },
        yAxis: {
            title: {
                
            }
        },
        plotOptions: {
            column: {
                cursor: 'pointer', 
                dataLabels: {
                    enabled: true,
                    color: colors[0],
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y +'';
                    }
                }
            }
        },
        tooltip: {
            formatter: function() { 
                s = this.x +':<b>'+ this.y +'</b>'; 
                return s;
            }
        },
        series: [{
            name: name,
            data: data3,
            color: 'white'
        }],
        exporting: {
            enabled: false
        }
    })
    .highcharts();
});				