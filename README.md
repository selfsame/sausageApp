SAUSAGE TODO


1.  Cap sausage ends with appropriate mesh based sausagey endings.
	
		Probably need another variable to indicate pristine geometry that is just rotated along the end normal.

		Draw sausages a little thicker to match their collision shape.

2.  Sausage chain stability.
	
	Maybe use rectangles instead of circles? Or capsules?  There needs to be more space between links.

	Need to find a good balance, ensure head and tail act the same (are different due to precision loss along the chain)

3.  Sausage controls.

	a) Moving an end should do a bit of magic to determine if it needs to rotate to go a certain direction, reversing should
	   have an interesting and believable result, either unwind a coil or snake the links like a spring.

	b) Get collision for each segment.  When an end moves, it can straighten it's side, but for acrobatic jumps it needs to have
	   contact points stored up around it.  So one half is touching the ground midway, that half could make a move strong enough to do a little jump,
	   which is strongest at the contact point.

	   [1]  This will need a lot of tweaks and algorithms to make 'fun', and somewhat realistic.

4.  Level features.
	
	a) certain types of game features should be placeable in the level editor, as well as some UI for adding game types that are playable
	   on that map.

5.  Box2D polygons from map.json need to be a closed loop, this is a similar problem to marking wire edges not rendering the entire marked selection.

