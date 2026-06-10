from flask import Flask, request, jsonify
app = Flask(__name__)

@app.route('/health',methods=['GET'])
def health():
    return jsonify({"status": "ok"})

@app.route('/ingest/edgar',methods=['POST'])
def edgar_ingest():
    data=request.get_json()
    ticker=data['ticker']
    return jsonify({"status":"stub","ticker":ticker})

@app.route('/ingest/csv',methods=['POST'])
def csv_ingest():
    data=request.get_json()
    return jsonify({"status":"stub"})




if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)