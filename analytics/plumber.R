library(plumber)
source("trends.R")
source("report_gen.R")

#* @get /health
function(req){
    return(list(status="ok"))
}

#* @post /analyze
function(req){
    body<-jsonlite::fromJSON(req$postBody)
    ticker<-body$ticker
    ratios_list<-body$ratios
    tryCatch({
        result<-analyze_all_trends(ratios_list)
        return(generate_report(ticker,result))
    },error=function(e){
        cat("[R analytics] Error:", conditionMessage(e),"\n")
        return(list(error="Trend analysis failed"))
    })
    
}