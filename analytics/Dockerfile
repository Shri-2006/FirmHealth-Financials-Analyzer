FROM rocker/r-ver:4.3.1

WORKDIR /app
COPY . .

RUN Rscript -e "install.packages('remotes', repos='https://cran.r-project.org'); \
    remotes::install_version('plumber', version='1.2.2', repos='https://cran.r-project.org'); \
    remotes::install_version('jsonlite', version='1.8.8', repos='https://cran.r-project.org')"




CMD ["Rscript", "run.R"]
