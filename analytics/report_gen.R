generate_report<-function(ticker,trends){
    directions<-sapply(trends,function(t) t$direction)
    overallDirection<-names(which.max(table(directions)))
    riskFlag<-any(sapply(trends, function(t) t$direction == "declining" & t$volatility == TRUE))

    summary<-list(overallDirection=overallDirection,riskFlag=riskFlag)
    return(list(ticker=ticker,trends=trends,summary=summary))
}