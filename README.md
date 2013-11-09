SAUSAGE TODO

[SATURDAY]

1.  Fix up house:
	a) party room - simple wallpaper, clock, table w/ cake, presents, boom box, balloons
	b) kitchen - fridge - stove - sink - table w/ cloth - tiled floor - cabinets.
	c) basement
	d) fireplace (for secret path from roof)

2.  Get controllers to work on PC (swap axis, for snes controller use buttons for right joystick)

3. Sausage control:
	a) make sure force scales with length
	b) should be able to 'stand up' if head is vertical and control stops?
	c) if two links get separated should try to reverse forces to keep together, otherwise splits the sausage.
	d) other safeguards (crosses over self, inside of a static polygon.)

4. Camera should use FOV for zoom, so zooming out keeps the perspective from obscuring sausages.

5. Implement StarCollect scenario with reappearing star pickups, keep score for players, show score/player avatar, show dedicated timer.

6.  Platformer level, with game modes for single/multiplayer.

7. Fix rotating game objects

8. dynamic polygon objects

9. shorten sausage links, fix midpoint spread and use openGL z depth to overwrite concave curve.

10. [DONE] sort alpha objects by z position during level compile.

11. instanced objects.

12. [DONE] remember filepath and name (needs to be relative though)



-----------------


1.  Cap sausage ends with appropriate mesh based sausagey endings. [DONE]
	
		[X] Caps are drawn via a separate shader, have player color and whatnot.

		Draw sausages a little thicker to match their collision shape.

2.  Sausage chain stability.
	
	[X] Maybe use rectangles instead of circles? Doesn't degrade the feel too much and buys me 2x the length.

	Need to find a good balance, ensure head and tail act the same (are different due to precision loss along the chain)

3.  Sausage controls.

	a) Moving an end should do a bit of magic to determine if it needs to rotate to go a certain direction, reversing should
	   have an interesting and believable result, either unwind a coil or snake the links like a spring.

	b) [DONE] Get collision for each segment.  When an end moves, it can straighten it's side, but for acrobatic jumps it needs to have
	   contact points stored up around it.  So one half is touching the ground midway, that half could make a move strong enough to do a little jump,
	   which is strongest at the contact point.

	   [1]  This will need a lot of tweaks and algorithms to make 'fun', and somewhat realistic.

	   		a)  Each end should have a base potential velocity, enough to raise that half like a brontosaurus neck.

	   		b)  The two sides should share velocity magnitude, so that moving both ends up does not result in floaty flight.

	   		c)  Ground contact gives more potential for velocity to the link that has it stored up, it dissapates pretty quickly.

	   			c-1)  potential should be shared towards the tips, so ground contact in the middle would be used by the ends, but this is tricky to think of a balance for.

	   		e)  The ends are 'sticky', they have more friction.  This facilitates inchworm walking, cartwheels, walljumping, etc.

	   			e-1)  Precise ground contact can be used to facilitate horizontal movement for the sticky ends, if they catch a cliff they
	   					should be able to hang on, and gain a little ground until they can get their bulk up.

	   		f)  Ends move in the cardinal directions, but they could rotate to match it as well.  This would involve 'uncurling' the end by going against the predominant curve
	   		    instead of the shortest angle to match.  This also facilitates inchworm type movement where you can quickly angle down with the end.


4.  Level features.
	
	a) certain types of game features should be placeable in the level editor, as well as some UI for adding game types that are playable
	   on that map.

	   	a-1) 'areas' have the same structure as box2d static polygons, with a name string.  They are non collidable, and used for triggering location based events.

	   	a-2) 'props' are a collection of visual geometry and a 2d collision polygon or shape.  They don't display, but are placed by the scenario logic at locii.

	   	a-3) 'locii' are points with a name.  There can be multiple locii with the same name (like spawn points)

5.  [DONE] Box2D polygons from map.json need to be a closed loop

