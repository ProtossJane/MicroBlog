import subprocess
import sys

id = sys.argv[1]
message = sys.argv[2]
times = int(sys.argv[4])
optimized = sys.argv[3]
for i in range(times):
	subprocess.call(['java', '-jar','Client.jar', id, "POST:"+message+str(i)+" to "+"Server#"+str(id)], optimized);
