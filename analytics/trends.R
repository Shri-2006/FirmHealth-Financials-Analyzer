analyze_all_trends<-function(ratios_list){
    results<-lapply(ratios_list,analyze_single_trend)
    return (results)
}


analyze_single_trend <- function(values){
    periods<-seq_along(values)
    model <- lm(values ~ periods)
    slope <- coef(model)[["periods"]]
    r_squared <- summary(model)$r.squared
    std_dev<-sd(values)
    if(slope>0.01){
        direction <- "improving"
    }
    else if (slope<(-0.01)){
        direction <- "declining"
    }
    else{
        direction <- "stable"
    }

    if(std_dev>0.3){
        volatility<-TRUE
    }
    else{
        volatility<-FALSE
    }
    return (list(slope=slope,r_squared=r_squared,std_dev=std_dev,direction=direction,volatility=volatility))
}