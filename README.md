MicroBlog
=========
Simulate a microblog using paxos to reach censistency.
The application was launched on 5 instances of EC2 in 5 regions. The application used Paxos for reaching consensus of the total order of blogs posted. Each instance maintained exactly the same log history of blogs. It can tolerate non-melacious failures less than majority number of servers. Recover process is automatic. 
Performance improvement for Paxos: Running parallel multi-Paxos in each instance. Only keep the causal relation of
blogs. Blogs posted to different server can be concurrent. The order of blogs may be different in 5 instances.
