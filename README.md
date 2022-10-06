<pre>
# BandWidthTester
Uses iperf3 asyncronously for real-time performance measurements at the command-line

EXAMPLE USAGE
$ java -jar BandWidthTester-1.0-SNAPSHOT.jar localhost
Executing command /usr/local/bin/iperf3 --forceflush --connect-timeout 3000 -c localhost -O 2 -P 8 -t 10 :  ├──┤
Connecting to host localhost, port 5201
  Local Host/IP: ::1 remote Host/IP: ::1 Remote Port: 5201
  ──────────────────────────────────────────────────────────────────────────────
             0.00-1.00                      80.1  Gbits/sec (omitted)
      Running  5.00-6.00   + [         ]   48.4  Gbits/sec

...

Executing command /usr/local/bin/iperf3 --forceflush --connect-timeout 3000 -c localhost -O 2 -P 8 -t 10 :  ├──┤
Connecting to host localhost, port 5201
  Local Host/IP: ::1 remote Host/IP: ::1 Remote Port: 5201
  ──────────────────────────────────────────────────────────────────────────────
             0.00-1.00                      80.1  Gbits/sec (omitted)
      Running  9.00-10.00    [          ]   48.4  Gbits/sec
  ──────────────────────────────────────────────────────────────────────────────
  Results:   0.00-10.00                     48.5  Gbits/sec sender
  Results:   0.00-10.00                     48.5  Gbits/sec receiver
Return Code: 000

</pre>
