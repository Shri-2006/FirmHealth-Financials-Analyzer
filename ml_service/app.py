from flask import Flask, request, jsonify
from ingestor import fetch_metrics
from sanitizer import sanitize_csv

app = Flask(__name__)

@app.route('/health',methods=['GET'])
def health():
    return jsonify({"status": "ok"})

@app.route('/ingest/edgar',methods=['POST'])
def edgar_ingest():
    data=request.get_json()
    ticker=data['ticker']
    metrics=fetch_metrics(ticker)
    if metrics is None:
        return jsonify({"error":"Failed to fetch metrics"}),500
    return jsonify(metrics)
    

@app.route('/ingest/csv',methods=['POST'])
def csv_ingest():    
    file=request.files['file']
    metrics= sanitize_csv(file)
    if metrics is None:
        return jsonify({"error":"Failed to fetch csv metrics"}),500
    return jsonify(metrics)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)