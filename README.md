  <H1>BandWidthTester</H1>
  <p>
    <br/>
    <LI>
      Uses iperf3 asyncronously for real-time performance measurements
      at the command-line
    </LI>
  </p>
  <H1>
    EXAMPLE USAGE
  </H1>
  <br/>
  <pre>
$ LANG=C TERM=dumb java -jar BandWidthTester-1.0-SNAPSHOT.jar localhost
Executing command /usr/local/bin/iperf3 --forceflush --connect-timeout 3000 -c localhost -O 2 -P 8 -t 10 :  |...*
Connecting to host localhost, port 5201
  Local Host/IP: ::1 remote Host/IP: ::1 Remote Port: 5201
  ------------------------------------------------------------------------------
.......          0.00-1.00        70.6  Gbits/sec (omitted)
...   Running  1.00-1.00        40.6  Gbits/sec
...   Running  1.00-2.00        80.1  Gbits/sec
...   Running  2.00-3.00        84.1  Gbits/sec
...   Running  3.00-4.00        88.7  Gbits/sec
...   Running  4.00-5.00        89.1  Gbits/sec
...   Running  5.00-6.00        87.0  Gbits/sec
...   Running  6.00-7.00        88.8  Gbits/sec
...   Running  7.00-8.00        78.2  Gbits/sec
...   Running  8.00-9.00        79.4  Gbits/sec
   Running  9.00-10.00       76.4  Gbits/sec

  ------------------------------------------------------------------------------
Results:   0.00-10.00       83.3  Gbits/sec sender
Results:   0.00-10.00       83.3  Gbits/sec receiver
Return Code: 000
</pre>

