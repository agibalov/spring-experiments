<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!doctype html>
<html>
	<head>
		<link rel="stylesheet" href="/assets/bootstrap.min.css" />
		<title>Forms Experiment</title>
	</head>
	<body>
		<div class="container">
			<div class="page-header">
				<h3>Hello <small>there</small></h3>
			</div>
			<div class="row">		
				<div class="span12">
					<tiles:insertAttribute name="body" />
				</div>		
			</div>
			<hr>
			&copy; <a href="http://loki2302.me" target="_blank">loki2302</a>
		</div>		
	</body>
</html>